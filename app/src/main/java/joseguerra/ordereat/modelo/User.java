package joseguerra.ordereat.modelo;

public class User {
    private String nombre;
    private String password;
    private String phone;
    private String IsStaff;
    private String SecureCode;
    private String HomeAddress;

    public User() {
    }

    public User(String nombre, String password, String SecureCode) {
        this.nombre = nombre;
        this.password = password;
        IsStaff= "false";
        this.SecureCode= SecureCode;

    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        HomeAddress = homeAddress;
    }
}
