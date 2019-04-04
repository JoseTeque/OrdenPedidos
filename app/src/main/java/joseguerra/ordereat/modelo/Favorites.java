package joseguerra.ordereat.modelo;

public class Favorites {
    private String FoodId;
    private String Foodname;
    private String Foodimage;
    private String Fooddescription;
    private String Foodprice;
    private String Fooddiscount;
    private String FoodmenuId;
    private String UserPhone;

    public Favorites() {
    }

    public Favorites(String foodId, String foodname, String foodimage, String fooddescription, String foodprice, String fooddiscount, String foodmenuId, String UserPhone) {
        FoodId = foodId;
        Foodname = foodname;
        Foodimage = foodimage;
        Fooddescription = fooddescription;
        Foodprice = foodprice;
        Fooddiscount = fooddiscount;
        FoodmenuId = foodmenuId;
        this.UserPhone = UserPhone;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodname() {
        return Foodname;
    }

    public void setFoodname(String foodname) {
        Foodname = foodname;
    }

    public String getFoodimage() {
        return Foodimage;
    }

    public void setFoodimage(String foodimage) {
        Foodimage = foodimage;
    }

    public String getFooddescription() {
        return Fooddescription;
    }

    public void setFooddescription(String fooddescription) {
        Fooddescription = fooddescription;
    }

    public String getFoodprice() {
        return Foodprice;
    }

    public void setFoodprice(String foodprice) {
        Foodprice = foodprice;
    }

    public String getFooddiscount() {
        return Fooddiscount;
    }

    public void setFooddiscount(String fooddiscount) {
        Fooddiscount = fooddiscount;
    }

    public String getFoodmenuId() {
        return FoodmenuId;
    }

    public void setFoodmenuId(String foodmenuId) {
        FoodmenuId = foodmenuId;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        this.UserPhone = userPhone;
    }
}
