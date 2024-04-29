package kz.runtime.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import kz.runtime.jpa.entity.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class mainCreateProductJpa {


    static EntityManagerFactory factory;

    public static void main(String[] args) throws Exception{
        ioProduct();
    }

    public static void ioProduct() throws IOException, SQLException {
        factory = Persistence.createEntityManagerFactory("main");
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        Product product = new Product();
        System.out.print("Введите название товара: ");
        String productName = bufferedReader.readLine();
        System.out.println();
        product.setName(productName);

        System.out.print("Введите цену: ");
        String price = bufferedReader.readLine();
        System.out.println();
        product.setPrice(Double.parseDouble(price.trim()));

        System.out.println("Выберите категорию"); // jdbc
        List<Category> categories = getCategories();
        int i = 0;
        Map<Integer, Category> map = new HashMap<>();
        for (Category category : categories) {
            System.out.println(++i + " - " + category.getName());
            map.put(i, category);
        }
        System.out.print("Введите номер категории: ");
        String category = bufferedReader.readLine();
        System.out.println();
        Category categoryChosen = map.get(Integer.parseInt(category.trim()));
        product.setCategory(categoryChosen);


        System.out.println("Опишите характеристики: ");
        List<Characteristic> characteristics = categoryChosen.getCharacteristics();
        List<ProductCharacteristic> productCharacteristics = new ArrayList<>();
        for (Characteristic characteristic : characteristics) {
            System.out.print(characteristic.getName() + ": ");
            String description = bufferedReader.readLine();
            ProductCharacteristic productCharacteristic = new ProductCharacteristic();
            productCharacteristic.setProduct(product);
            productCharacteristic.setCharacteristic(characteristic);
            productCharacteristic.setDescription(description);
            productCharacteristics.add(productCharacteristic);
        }
        EntityManager manager = factory.createEntityManager();
        try{
            manager.getTransaction().begin();
            manager.persist(product);
            for (ProductCharacteristic productCharacteristic : productCharacteristics) {
                manager.persist(productCharacteristic);
            }
            manager.getTransaction().commit();
        }catch (Exception e){
            manager.getTransaction().rollback();
        }
    }

    public static List<Category> getCategories() throws SQLException {
        EntityManager manager = factory.createEntityManager();
        TypedQuery<Category> categoryQuery = manager.createQuery(
                "select c from Category c", Category.class
        );
        return categoryQuery.getResultList();
    }
}