package com.backend.warehouse.payload.request;

public class CreateShelfRequest {
    private String nameShelf;
    private String type;
    private Long warehouseId;
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private int layers = 2; // Mặc định tạo 2 tầng
    private int compartmentsPerLayer = 3; // Mặc định 3 ngăn mỗi tầng: Trái, Giữa, Phải

    public CreateShelfRequest() {}

    public String getNameShelf() { return nameShelf; }
    public void setNameShelf(String nameShelf) { this.nameShelf = nameShelf; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public float getxCoord() { return xCoord; }
    public void setxCoord(float xCoord) { this.xCoord = xCoord; }

    public float getyCoord() { return yCoord; }
    public void setyCoord(float yCoord) { this.yCoord = yCoord; }

    public float getzCoord() { return zCoord; }
    public void setzCoord(float zCoord) { this.zCoord = zCoord; }

    public int getLayers() { return layers; }
    public void setLayers(int layers) { this.layers = layers; }

    public int getCompartmentsPerLayer() { return compartmentsPerLayer; }
    public void setCompartmentsPerLayer(int compartmentsPerLayer) { this.compartmentsPerLayer = compartmentsPerLayer; }
}
