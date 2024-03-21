package ro.tuc.ds2020.Rabbit_Energy_Sent;




public class CustomMessage {
    private String timestamp;
    private String device_id;
    private double measurement_value;

    public CustomMessage(String timestamp, String device_id, double measurement_value) {
        this.timestamp = timestamp;
        this.device_id = device_id;
        this.measurement_value = measurement_value;
    }
    public CustomMessage(){

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public double getMeasurement_value() {
        return measurement_value;
    }

    public void setMeasurement_value(double measurement_value) {
        this.measurement_value = measurement_value;
    }
}





