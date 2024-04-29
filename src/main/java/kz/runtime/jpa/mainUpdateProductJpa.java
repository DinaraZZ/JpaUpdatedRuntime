package kz.runtime.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import kz.runtime.jpa.entity.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mainUpdateProductJpa {
    static EntityManagerFactory factory;

    public static void main(String[] args) throws IOException {
        // редактировние товара
        // id +
        // название +
        // стоимость +
        // все значения характеристики
        ioProduct();
    }

    public static void ioProduct() throws IOException {
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

            System.out.println();
            System.out.println("Текущее название товара: " + product.getName());
            System.out.print("Введите новое название товара: ");
            String productNameNew = bufferedReader.readLine();
            if (!productNameNew.isBlank()) product.setName(productNameNew);

            System.out.println();
            System.out.println("Текущая цена товара: " + product.getPrice());
            System.out.print("Введите новую цену товара: ");
            String productPriceNew = bufferedReader.readLine();
            if (!productPriceNew.isBlank()) product.setPrice(Double.parseDouble(productPriceNew.trim()));

            List<Characteristic> characteristics = product.getCategory().getCharacteristics();

            System.out.println();
            for (Characteristic characteristic : characteristics) {
                System.out.printf("%s \"%s\": ", "Текущее значение характеристики",
                        characteristic.getName());

                ProductCharacteristic productCharacteristic = new ProductCharacteristic();
                try {
                    productCharacteristic = getCharacteristicDescription(
                            characteristic.getId(), product.getId(), manager);
                    System.out.println(productCharacteristic.getDescription());
                } catch (NoResultException exception) {
                    System.out.println("(описание отсутствует)");
                }

                System.out.print("Введите новое значение: ");
                String characteristicDescriptionNew = bufferedReader.readLine();
                if (!characteristicDescriptionNew.isBlank()) {
                    productCharacteristic.setDescription(characteristicDescriptionNew);
                    productCharacteristic.setProduct(product);
                    productCharacteristic.setCharacteristic(characteristic);
                    manager.persist(productCharacteristic);
                }
                System.out.println();
            }

            manager.persist(product);
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

    public static List<ProductCharacteristic> getProductCharacteristics(Long productId, EntityManager manager) {

        TypedQuery<ProductCharacteristic> characteristicTypedQuery = manager.createQuery(
                "select p from ProductCharacteristic p where p.product.id = ?1", ProductCharacteristic.class
        );
        characteristicTypedQuery.setParameter(1, productId);
        return characteristicTypedQuery.getResultList();
    }

    public static ProductCharacteristic getCharacteristicDescription(Long characteristicId, Long productId, EntityManager manager) {
        TypedQuery<ProductCharacteristic> productCharacteristicQuery = manager.createQuery(
                """
                        select pc from ProductCharacteristic pc
                        where pc.product.id = ?1
                        and pc.characteristic.id = ?2
                        """, ProductCharacteristic.class
        );
        productCharacteristicQuery.setParameter(1, productId);
        productCharacteristicQuery.setParameter(2, characteristicId);
        return productCharacteristicQuery.getSingleResult();
    }
}