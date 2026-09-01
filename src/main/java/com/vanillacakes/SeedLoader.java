package com.vanillacakes;

import com.vanillacakes.cakes.Cake;
import com.vanillacakes.cakes.CakeImage;
import com.vanillacakes.cakes.CakeImageRepository;
import com.vanillacakes.cakes.CakeRepository;
import com.vanillacakes.transactions.TransactionManager;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class SeedLoader {

    private final TransactionManager transactionManager;

    public SeedLoader(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void load() {
        transactionManager.execute(connection -> {
            CakeRepository cakeRepository = new CakeRepository(connection);
            CakeImageRepository cakeImageRepository = new CakeImageRepository(connection);

            if (cakeRepository.existsAny()) {
                return true;
            }

            Cake sweetheartVanillaRaspberry = createCake("Sweetheart Vanilla Raspberry",
                    "A charming, rustic centerpiece that brings together the comforting warmth of vanilla with the bright, tangy punch of fresh summer berries.",
                    new BigDecimal("29.99"),
                    cakeRepository
            );
            saveImage(sweetheartVanillaRaspberry, "/seed/sweetheart_vanilla_raspberry.jpg", "image/jpeg", cakeImageRepository);

            Cake classicVanillaHomestyleStrawberryDream = createCake("Classic Vanilla Homestyle Strawberry Dream",
                    "Take a journey back to the best memories with our Classic Vanilla Homestyle Strawberry Dream cake. Reminiscent of perfect summers and cozy kitchens, this cake features layers of tender, moist cake filled with rich strawberry goodness.",
                    new BigDecimal("32.49"),
                    cakeRepository
            );
            saveImage(classicVanillaHomestyleStrawberryDream, "/seed/classic_vanilla_homestyle_strawberry_dream.png", "image/png", cakeImageRepository);

            Cake vanillaSaltedCaramelPecanCrunch = createCake("Vanilla Salted Caramel and Pecan Crunch",
                    "Satisfy your cravings with the ultimate sweet and savory combination. Enrobed in a smooth vanilla cream frosting and finished with a golden caramel drip, it offers a deep, cozy flavor profile in every bite.",
                    new BigDecimal("27.89"),
                    cakeRepository
            );
            saveImage(vanillaSaltedCaramelPecanCrunch, "/seed/vanilla_salted_caramel_and_pecan_crunch.png", "image/png", cakeImageRepository);

            Cake lemonZest = createCake("Lemon Zest",
                    "Bright citrus meets classic sweetness in this perfectly balanced dessert. Fluffy vanilla cake layers infused with real Madagascar vanilla beans are paired with a fresh, tangy Meyer lemon curd. Finished with a velvety vanilla-lemon frosting and garnished with candied lemon slices, every slice delivers a crisp, refreshing burst of flavor with a warm vanilla note.",
                    new BigDecimal("22.49"),
                    cakeRepository
            );
            saveImage(lemonZest, "/seed/lemon_zest.jpg", "image/jpeg", cakeImageRepository);

            Cake pistachioBlossom = createCake("Pistachio Blossom",
                    "A refined profile blending nutty richness with floral warmth. Layers of delicate cake infused with pure Madagascar vanilla bean paste are layered with a silky pistachio praline cream. Enrobed in a smooth vanilla buttercream and garnished with crushed roasted pistachios, every bite yields a subtle, sweet crunch paired with a soft vanilla finish.",
                    new BigDecimal("27.99"),
                    cakeRepository
            );
            saveImage(pistachioBlossom, "/seed/pistachio_blossom.png", "image/png", cakeImageRepository);

            Cake darkChocolateGanache = createCake("Dark Chocolate Ganache",
                    "Deep cacao intensity grounded in smooth, comforting warmth. Delicate cake layers infused with natural vanilla bean are filled with a rich 70% dark chocolate ganache. Wrapped in a sleek vanilla buttercream and finished with a glossy chocolate drip, this dessert strikes a flawless harmony between bold, bittersweet cocoa and soft, sweet vanilla notes.",
                    new BigDecimal("42.99"),
                    cakeRepository
            );
            saveImage(darkChocolateGanache, "/seed/dark_chocolate_ganache.png", "image/png", cakeImageRepository);

            Cake spicedChaiVelvet = createCake("Spiced Chai Velvet",
                    "Warm aromatic spices meet classic, velvety sweetness. Fluffy cake layers steeped with real Madagascar vanilla bean are paired with a fragrant chai-infused cream filling packed with notes of cinnamon, cardamom, and clove. Wrapped in a light vanilla frosting and dusted with fine spice, this dessert offers a comforting, chai-latte-inspired bite with a smooth vanilla finish.",
                    new BigDecimal("39.49"),
                    cakeRepository
            );
            saveImage(spicedChaiVelvet, "/seed/spiced_chai_velvet.png", "image/png", cakeImageRepository);

            Cake honeyLavender = createCake("Honey Lavender",
                    "An enchanting blend of floral notes and golden sweetness. Enrobed in a smooth vanilla buttercream and garnished with dried lavender buds, each slice delivers a soothing, delicate flavor profile.",
                    new BigDecimal("48.99"),
                    cakeRepository
            );
            saveImage(honeyLavender, "/seed/honey_lavender.png", "image/png", cakeImageRepository);

            Cake blueberryEarlGrey = createCake("Blueberry Earl Grey",
                    "A sophisticated pairing of citrusy tea notes and bright summer fruit. Delicate cake layers infused with natural vanilla bean are layered with an Earl Grey tea-infused cream and a sweet, house-made blueberry compote. Wrapped in a smooth vanilla buttercream and crowned with fresh, plump blueberries, this cake offers a beautifully balanced, aromatic flavor experience.",
                    new BigDecimal("36.99"),
                    cakeRepository
            );
            saveImage(blueberryEarlGrey, "/seed/blueberry_earl_grey.png", "image/png", cakeImageRepository);

            Cake coconutCreamDreamCake = createCake("Coconut Cream Dream Cake",
                    "A tropical, velvety escape wrapped in classic sweetness. Wrapped in a light vanilla buttercream and coated in delicate, snowy coconut shavings, each bite offers a soft, nutty crunch with a smooth vanilla finish.",
                    new BigDecimal("38.99"),
                    cakeRepository
            );
            saveImage(coconutCreamDreamCake, "/seed/coconut_cream_dream_cake.png", "image/png", cakeImageRepository);

            createCake(
                    "Classic Chocolate Vanilla Drip",
                    "Experience the perfect harmony of baking’s most beloved flavors. This stunning centerpiece blends nostalgic comfort with a sophisticated, modern aesthetic, making it the ultimate showstopper for any celebration.",
                    new BigDecimal("24.99"),
                    cakeRepository
            );
            //This cake has no image

            return true; //Mock return because execute has a return
        });
    }

    private Cake createCake(String name, String description, BigDecimal price, CakeRepository cakeRepository) {
        Cake cake = new Cake(name, description, price, true);
        return cakeRepository.save(cake);
    }

    private void saveImage(Cake cake, String resourcePath, String mimeType, CakeImageRepository cakeImageRepository) {
        try (InputStream inputStream =
                     getClass().getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Could not find resource: " + resourcePath);
            }

            byte[] content = inputStream.readAllBytes();

            CakeImage cakeImage = new CakeImage(null, cake.getId(), mimeType, content);

            cakeImageRepository.save(cakeImage);
        } catch (IOException e) {
            throw new RuntimeException("Could not load image: " + resourcePath, e);
        }
    }
}
