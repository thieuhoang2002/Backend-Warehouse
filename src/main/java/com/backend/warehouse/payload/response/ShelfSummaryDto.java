package com.backend.warehouse.payload.response;

public class ShelfSummaryDto {
    private Long shelfId;
    private String nameShelf;
    private String type;
    private Long warehouseId;
    private String warehouseName;
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private int compartmentCount;
    private boolean hasItems;

    public ShelfSummaryDto() {}

    public ShelfSummaryDto(Long shelfId, String nameShelf, String type, Long warehouseId,
                           String warehouseName, float xCoord, float yCoord, float zCoord,
                           int compartmentCount, boolean hasItems) {
        this.shelfId = shelfId;
        this.nameShelf = nameShelf;
        this.type = type;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.compartmentCount = compartmentCount;
        this.hasItems = hasItems;
    }

    public Long getShelfId() { return shelfId; }
    public void setShelfId(Long shelfId) { this.shelfId = shelfId; }

    public String getNameShelf() { return nameShelf; }
    public void setNameShelf(String nameShelf) { this.nameShelf = nameShelf; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public float getxCoord() { return xCoord; }
    public void setxCoord(float xCoord) { this.xCoord = xCoord; }

    public float getyCoord() { return yCoord; }
    public void setyCoord(float yCoord) { this.yCoord = yCoord; }

    public float getzCoord() { return zCoord; }
    public void setzCoord(float zCoord) { this.zCoord = zCoord; }

    public int getCompartmentCount() { return compartmentCount; }
    public void setCompartmentCount(int compartmentCount) { this.compartmentCount = compartmentCount; }

    public boolean isHasItems() { return hasItems; }
    public void setHasItems(boolean hasItems) { this.hasItems = hasItems; }
}
