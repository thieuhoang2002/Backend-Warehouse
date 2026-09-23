package com.backend.warehouse.controller;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.warehouse.payload.response.BookingResponse;
import com.backend.warehouse.payload.response.MessageResponse;
import com.backend.warehouse.repository.BookingRepository;
import com.backend.warehouse.service.BookingServiceImpl;
import com.backend.warehouse.service.ItemServiceImpl;
import com.backend.warehouse.service.R2StorageService;
import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

	@Autowired
	private BookingServiceImpl bookingService;

	@Autowired
	private ItemServiceImpl itemService;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private R2StorageService r2StorageService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFormData(@RequestParam("file") MultipartFile file) {
		try {
			bookingService.saveFormData(file);
			return ResponseEntity.ok(new MessageResponse("Dữ liệu đã được lưu thành công!"));
		} catch (IOException e) {
			return ResponseEntity.status(500)
					.body(new MessageResponse("Có lỗi xảy ra khi lưu dữ liệu: " + e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(400).body(new MessageResponse("Dữ liệu không hợp lệ: " + e.getMessage()));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<BookingResponse>> getAllBookings() {
		List<BookingResponse> bookings = bookingService.getAllBookings();
		return ResponseEntity.ok(bookings);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateBooking(
      @PathVariable("id") String id,
      @RequestParam("email") String email,
      @RequestParam("phoneNumber") String phoneNumber,
      @RequestParam("fullName") String fullName,
      @RequestParam("filePath") String filePath
	) {
      try {
          bookingService.updateBooking(id, email, phoneNumber, fullName, filePath);
          return ResponseEntity.ok(new MessageResponse("Cập nhật booking thành công!"));
      } catch (IOException e) {
          return ResponseEntity
              .status(500) 
              .body(new MessageResponse("Có lỗi xảy ra khi cập nhật dữ liệu: " + e.getMessage()));
      } catch (Exception e) {
          return ResponseEntity
              .status(400) 
              .body(new MessageResponse("Dữ liệu không hợp lệ: " + e.getMessage()));
      }
  }
	
  @GetMapping("/download/{fileName:.+}")
  public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
      try {
          InputStream stream = r2StorageService.downloadFile(fileName);
          // Tên file hiển thị khi download = tên object key (bỏ prefix nếu có)
          String displayName = fileName.contains("/") ? fileName.substring(fileName.lastIndexOf('/') + 1) : fileName;
          return ResponseEntity.ok()
                  .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + displayName + "\"")
                  .contentType(MediaType.APPLICATION_OCTET_STREAM)
                  .body(new InputStreamResource(stream));
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
  }

  @GetMapping("/totalCustomers")
  public ResponseEntity<Long> getTotalCustomers() {
      Long totalCustomers = bookingService.getTotalCustomers();
      return ResponseEntity.ok(totalCustomers);
  }
  
	public Long parseId(String formattedId) {
	    if (formattedId.startsWith("BK")) {
	        String numericPart = formattedId.substring(2); 
	        return Long.parseLong(numericPart);
	    } else {
	        throw new IllegalArgumentException("Invalid formatted ID: " + formattedId);
	    }
	}
	
  
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteBooking(@PathVariable("id") String id) {
      try {
          Long bookingId = parseId(id);

          // Lấy R2 key trước khi xóa booking khỏi DB
          String r2Key = bookingRepository.findById(bookingId)
                  .map(b -> b.getExcelFile())
                  .orElse(null);

          // Xóa items liên quan
          itemService.deleteItemsByBookingId(bookingId);

          // Xóa booking khỏi DB
          bookingService.deleteBookingById(bookingId);

          // Xóa file CSV khỏi Cloudflare R2
          if (r2Key != null && !r2Key.isBlank()) {
              try {
                  r2StorageService.deleteFile(r2Key);
                  System.out.println("[Booking] Đã xóa file R2: " + r2Key);
              } catch (Exception e) {
                  // Không fail cả request nếu R2 xóa lỗi (ví dụ: file đã bị xóa thủ công)
                  System.err.println("[Booking] Warning: Không thể xóa file R2 '" + r2Key + "': " + e.getMessage());
              }
          }

          return ResponseEntity.ok(new MessageResponse("Xóa booking và các item liên quan thành công!"));
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new MessageResponse("Có lỗi xảy ra khi xóa booking: " + e.getMessage()));
      }
  }

  
}
