package com.backend.warehouse.payload.response;

public class WarehouseSummaryDto {
    private Long warehouseId;
    private String name;
    private String location;
    private int shelfCount;
    private int compartmentCount;
    private int itemCount;

    public WarehouseSummaryDto() {}

    public WarehouseSummaryDto(Long warehouseId, String name, String location, int shelfCount, int compartmentCount, int itemCount) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.location = location;
        this.shelfCount = shelfCount;
        this.compartmentCount = compartmentCount;
        this.itemCount = itemCount;
    }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getShelfCount() { return shelfCount; }
    public void setShelfCount(int shelfCount) { this.shelfCount = shelfCount; }

    public int getCompartmentCount() { return compartmentCount; }
    public void setCompartmentCount(int compartmentCount) { this.compartmentCount = compartmentCount; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}
