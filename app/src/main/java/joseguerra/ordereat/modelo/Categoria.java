package joseguerra.ordereat.modelo;

public class Categoria {

    private String Imagen;
    private String name;

    public Categoria() {
    }

    public Categoria(String imagen, String name) {
        Imagen = imagen;
        this.name = name;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
