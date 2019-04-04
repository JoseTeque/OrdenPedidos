package joseguerra.ordereat.modelo;

import java.util.List;

public class Restaurantes {

    private String name;
    private String imagen;


    public Restaurantes() {
    }

    public Restaurantes(String name, String imagen) {
        this.name = name;
        this.imagen = imagen;
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
}
