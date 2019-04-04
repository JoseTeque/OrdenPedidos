package joseguerra.ordereat.modelo;

public class Order {

    private String userPhone;
    private String ProductoId;
    private String NombreProducto;
    private String Cantidad;
    private String Precio;
    private String Descuento;
    private String Image;

    public Order() {
    }


    public Order(String userPhone, String productoId, String nombreProducto, String cantidad, String precio, String descuento,String image) {
        this.userPhone = userPhone;
        ProductoId = productoId;
        NombreProducto = nombreProducto;
        Cantidad = cantidad;
        Precio = precio;
        Descuento = descuento;
        Image= image;

    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getProductoId() {
        return ProductoId;
    }

    public void setProductoId(String productoId) {
        ProductoId = productoId;
    }

    public String getNombreProducto() {
        return NombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        NombreProducto = nombreProducto;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getDescuento() {
        return Descuento;
    }

    public void setDescuento(String descuento) {
        Descuento = descuento;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
