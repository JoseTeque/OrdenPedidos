package joseguerra.ordereat.modelo;

public class Banner {

    private String name;
    private String imagen;
    private String id;

    public Banner() {
    }

    public Banner(String name, String imagen, String id) {
        this.name = name;
        this.imagen = imagen;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
