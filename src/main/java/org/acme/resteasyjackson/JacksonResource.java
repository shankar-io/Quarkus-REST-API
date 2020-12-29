package org.acme.resteasyjackson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

@Path("/resteasy-jackson/quarks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonResource {

    private final Set<Product> products = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public JacksonResource() {
        products.add(new Product("Maxi Scraper", "test",10));
        products.add(new Product("Disney", "testing", 2));
        products.add(new Product("Pike 1000 Fine Mist Sprayer", "Introducing Pike System's new, revolutionary fine-mist disinfectant", 5));
        products.add(new Product("???", null, 0));
    }

    @GET
    public Set<Product> list() {
        return products;
    }

    @POST
    public Set<Product> add(Product product) {
        products.add(product);
        return products;
    }

    @DELETE
    public Set<Product> delete(Product product) {
        products.removeIf(existingQuark -> existingQuark.name.contentEquals(product.name));
        return products;
    }

    public  static class Product{
        public String name;
        public String description;
        public int quantity;

        public Product(){

        }

        public Product(String name, String description, int quantity){
            this.name = name;
            this.description = description;
            this.quantity = quantity;
        }
    }

}
