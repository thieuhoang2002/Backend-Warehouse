package com.backend.warehouse.service;

import com.backend.warehouse.entity.Booking;
import com.backend.warehouse.entity.Item;
import com.backend.warehouse.payload.response.BookingResponse;
import com.backend.warehouse.repository.BookingRepository;
import com.backend.warehouse.repository.ItemRepository;

import io.jsonwebtoken.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemService itemservice;

	@Autowired
	private R2StorageService r2StorageService;

	private String[] header = { "Name", "Type", "Quantity", "Weight (g)", "Checkin Date", "Checkout Date", "Image", "Useremail", "Delivery" };

	@Override
	@Transactional
	public void saveFormData(MultipartFile file) throws IOException, java.io.IOException {

		// 1. Upload file lên Cloudflare R2, lấy key (tên object)
		String r2Key = r2StorageService.uploadFile(file);

		// 2. Tạo booking, lưu key R2 thay vì đường dẫn local
		Booking booking = new Booking();
		booking.setExcelFile(r2Key);

		// 3. Đọc CSV lần 1: lấy email khách hàng + referenceNo
		if (file != null && !file.isEmpty()) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
					CSVParser csvParser = new CSVParser(reader,
							CSVFormat.DEFAULT.builder().setHeader(header).setSkipHeaderRecord(true).build())) {

				for (CSVRecord csvRecord : csvParser) {
					booking.setCustomerEmail(csvRecord.get("Useremail"));
				}
			}

			Random random = new Random();
			Long randomNumber = 10000 + random.nextLong(90000);
			booking.setReferenceNo(randomNumber);
		}

		Booking savedBooking = bookingRepository.save(booking);

		System.out.println("File đã được upload lên R2 với key: " + r2Key);

		// 4. Đọc CSV lần 2: tạo các Item
		if (file != null && !file.isEmpty()) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
					CSVParser csvParser = new CSVParser(reader,
							CSVFormat.DEFAULT.builder().setHeader(header).setSkipHeaderRecord(true).build())) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				for (CSVRecord csvRecord : csvParser) {
					Item item = new Item();
					item.setName(csvRecord.get("Name"));
					item.setType(csvRecord.get("Type"));
					item.setQuantity(Integer.parseInt(csvRecord.get("Quantity").replace(".", "")));
					item.setCheckin(LocalDate.parse(csvRecord.get("Checkin Date"), formatter));
					item.setCheckout(LocalDate.parse(csvRecord.get("Checkout Date"), formatter));
					String image = csvRecord.get("Image");
					item.setImage(image.isEmpty() ? null : image);
					item.setBooking(savedBooking);
					item.setWeight(Float.parseFloat(csvRecord.get("Weight (g)").replace(".", "")));
					String delivery = csvRecord.get("Delivery");
					item.setDelivery(delivery.isEmpty() ? null : delivery);
					item.setStatus("Đang lưu kho");
					itemRepository.save(item);
				}
			}
		}
	}


	public String formatId(Long id) {
	    if (id < 10) {
	        return "BK000" + id;
	    } else if (id < 100) {
	        return "BK00" + id;
	    } else if (id < 1000) {
	        return "BK0" + id;
	    } else {
	        return "BK" + id;
	    }
	}

	public Long parseId(String formattedId) {
	    if (formattedId.startsWith("BK")) {
	        String numericPart = formattedId.substring(2); 
	        return Long.parseLong(numericPart);
	    } else {
	        throw new IllegalArgumentException("Invalid formatted ID: " + formattedId);
	    }
	}
	
	@Override
	public List<BookingResponse> getAllBookings() {
		
	    List<Long> bookingIds = itemservice.getBookingIdsFromItems();
	    Set<Long> bookingIdSet = new HashSet<>(bookingIds);

	    // In kết quả ra console
	    System.out.println("Booking IDs: " + bookingIds);
	    
	    // Lọc các booking từ danh sách bookingIds
	    List<Booking> bookings = bookingRepository.findAll().stream()
	        .filter(booking -> bookingIdSet.contains(booking.getId()))  // Dùng Set để kiểm tra nhanh hơn
	        .collect(Collectors.toList());

	    // Chuyển các booking thành bookingDTO
	    List<BookingResponse> bookingDTOs = bookings.stream().map(booking -> 
	        new BookingResponse(
	            formatId(booking.getId()), 
	            booking.getCustomerEmail(),
	            booking.getCustomerName(),
	            booking.getNumberphone(),
	            booking.getExcelFile(),
	            booking.getReferenceNo() 
	        )
	    ).collect(Collectors.toList());

	    return bookingDTOs;
	}

	@Override
	@Transactional
	public Booking updateBooking(String id, String email, String phoneNumber, String fullName, String filePath) throws IOException {
	Booking booking = bookingRepository.findById(parseId(id))
	      .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + id));
	  
	booking.setCustomerEmail(email);
	booking.setNumberphone(phoneNumber);
	booking.setCustomerName(fullName);
	booking.setExcelFile(filePath);
	
	return bookingRepository.save(booking);
	}

	public Long getTotalCustomers() {
        Long totalCustomers = bookingRepository.countTotalCustomers();
        return totalCustomers != null ? totalCustomers : 0;
    }

	@Transactional
	public void deleteBookingById(Long id) {
        bookingRepository.deleteById(id);
    }
}
