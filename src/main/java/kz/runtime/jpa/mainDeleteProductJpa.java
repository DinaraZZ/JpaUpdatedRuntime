package kz.runtime.jpa;

import jakarta.persistence.*;
import kz.runtime.jpa.entity.Product;
import kz.runtime.jpa.entity.ProductCharacteristic;
import org.hibernate.engine.spi.SessionLazyDelegator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mainDeleteProductJpa {
    static EntityManagerFactory factory;

    public static void main(String[] args) {
        ioDeleteProduct();
    }

    public static void ioDeleteProduct() {
        factory = Persistence.createEntityManagerFactory("main");
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();

            List<Product> products = getProducts(manager);
            Map<Integer, Product> productMap = new HashMap<>();
            int i = 1;
            for (Product product : products) {
                System.out.printf("%-3d %s\n", i, product.getName());
                productMap.put(i++, product);
            }
            System.out.print("Выберите номер товара: ");
            String productNumber = bufferedReader.readLine();
            Product product = productMap.get(Integer.parseInt(productNumber.trim()));

            List<ProductCharacteristic> productCharacteristics = product.getCharacteristicDescription();
            for (ProductCharacteristic characteristic : productCharacteristics) {
                manager.remove(characteristic);
            }

            manager.remove(product);
            manager.getTransaction().commit();

        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }

    public static List<Product> getProducts(EntityManager manager) {
        TypedQuery<Product> productTypedQuery = manager.createQuery(
                "select p from Product p", Product.class
        );
        return productTypedQuery.getResultList();
    }

}
