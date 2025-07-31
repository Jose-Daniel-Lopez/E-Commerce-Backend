package com.app.data.seeder;

import com.app.entities.Category;
import com.app.entities.Product;
import com.app.entities.ProductVariant;
import com.app.repositories.CategoryRepository;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductVariantRepository;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.app.data.seeder.ProductConstants.*;

public class ProductSeeder {

    private static final int NUM_PRODUCTS_PER_CATEGORY = 15;

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductVariantRepository productVariantRepo;
    private final Faker faker;
    private final Random random = new Random();

    public ProductSeeder(ProductRepository productRepo, CategoryRepository categoryRepo, ProductVariantRepository productVariantRepo, Faker faker) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.productVariantRepo = productVariantRepo;
        this.faker = faker;
    }

    public void seed() {
        if (productRepo.count() == 0) {
            seedProducts();
        }
        if (productVariantRepo.count() == 0) {
            seedProductVariants();
        }
    }

    private void seedProducts() {
        System.out.println("Seeding products with realistic names and modern attributes...");
        List<Category> categories = categoryRepo.findAll();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Skipping product seeding.");
            return;
        }
        Set<String> allBrandsSet = new HashSet<>();
        CATEGORY_BRANDS.values().forEach(allBrandsSet::addAll);
        List<String> allBrands = new ArrayList<>(allBrandsSet);
        List<Product> products = new ArrayList<>();
        for (Category category : categories) {
            String categoryName = category.getName();
            List<String> validBrands = CATEGORY_BRANDS.getOrDefault(categoryName, allBrands);
            // Filter brands that are valid for this category
            List<String> filteredBrands = validBrands.stream()
                    .filter(brand -> isValidBrandCategory(brand, categoryName))
                    .collect(Collectors.toList());
            if (filteredBrands.isEmpty()) filteredBrands = validBrands;

            for (int i = 0; i < NUM_PRODUCTS_PER_CATEGORY; i++) {
                String brand = filteredBrands.get(random.nextInt(filteredBrands.size()));
                // Use brand-specific naming rules
                List<String> namingTemplates = BRAND_NAMING_RULES.getOrDefault(brand, BRAND_NAMING_RULES.get("DEFAULT"));
                String template = namingTemplates.get(random.nextInt(namingTemplates.size()));
                String productName = injectRealisticValues(template, brand, categoryName);

                double minPrice = 50.00;
                double maxPrice = 2500.00;
                double randomPrice = minPrice + (maxPrice - minPrice) * random.nextDouble();
                BigDecimal price = BigDecimal.valueOf(randomPrice).setScale(2, RoundingMode.HALF_UP);

                Product.ProductBuilder productBuilder = Product.builder()
                        .name(productName)
                        .brand(brand)
                        .description(faker.lorem().sentence(10))
                        .createdAt(randomCreatedAt(random))
                        .rating(faker.number().randomDouble(1, 0, 5))
                        .isFeatured(random.nextBoolean() && i < 3)
                        .basePrice(price)
                        .totalStock(0)
                        .category(category);

                // === MOBILE & COMPUTE CATEGORIES ===
                if (List.of("Smartphones", "Tablets", "Computers").contains(categoryName)) {
                    String os = "";
                    String cpu = "";
                    Integer ram = null;
                    String storage = "";
                    String gpu = "Integrated Graphics";
                    Integer refreshRate = 60;

                    switch (categoryName) {
                        case "Smartphones":
                            os = "Apple".equals(brand) ? "iOS" : "Android";
                            if ("Apple".equals(brand)) {
                                cpu = faker.options().option("A16 Bionic", "A17 Pro");
                            } else if ("Google".equals(brand)) {
                                cpu = "Google Tensor G3";
                            } else if ("Samsung".equals(brand)) {
                                cpu = faker.options().option("Exynos 2400", "Snapdragon 8 Gen 3 for Galaxy");
                            } else {
                                cpu = faker.options().option("Snapdragon 8 Gen 3", "Dimensity 9300");
                            }
                            Integer[] ramOptions = {8, 12, 16};
                            ram = ramOptions[random.nextInt(ramOptions.length)];
                            storage = faker.options().option("128GB", "256GB", "512GB", "1TB");
                            gpu = faker.options().option("Adreno 750", "Mali-G72", "Apple GPU 6-Core");
                            Integer[] refreshRateOptions = {60, 90, 120};
                            refreshRate = refreshRateOptions[random.nextInt(refreshRateOptions.length)];
                            break;

                        case "Tablets":
                            os = "Apple".equals(brand) ? "iPadOS" :
                                    "Microsoft".equals(brand) ? "Windows 11" : "Android";
                            cpu = faker.options().option("Apple M2", "Snapdragon 8 Gen 3", "MediaTek Kompanio");
                            Integer[] tabletRamOptions = {8, 12, 16};
                            ram = tabletRamOptions[random.nextInt(tabletRamOptions.length)];
                            storage = faker.options().option("64GB eMMC", "128GB SSD", "256GB NVMe", "512GB UFS");
                            gpu = faker.options().option("Apple M2 GPU", "Adreno 780", "Mali-G715");
                            Integer[] tabletRefreshRateOptions = {60, 90, 120};
                            refreshRate = tabletRefreshRateOptions[random.nextInt(tabletRefreshRateOptions.length)];
                            break;

                        case "Computers":
                            os = "Apple".equals(brand) ? "macOS" : faker.options().option("Windows 11", "Linux");
                            if ("Apple".equals(brand)) {
                                cpu = faker.options().option("M2 Pro", "M3", "M3 Pro", "M3 Max");
                                gpu = "Apple Integrated Graphics";
                            } else {
                                cpu = faker.options().option("Intel Core Ultra 7", "AMD Ryzen 7", "Intel Core i7");
                                gpu = faker.options().option("NVIDIA RTX 4070", "AMD Radeon RX 7800M", "Intel Arc Graphics");
                            }
                            Integer[] computerRamOptions = {16, 32, 64};
                            ram = computerRamOptions[random.nextInt(computerRamOptions.length)];
                            storage = faker.options().option("512GB SSD", "1TB NVMe", "2TB SSD");
                            refreshRate = 60; // desktops usually 60Hz
                            break;
                    }

                    // Set Mobile & Compute fields
                    productBuilder
                            .cpu(cpu)
                            .ram(ram)
                            .storage(storage)
                            .gpu(gpu)
                            .os(os)
                            .screenSize(faker.options().option("6.1\"", "6.7\"", "10.5\"", "13.3\"", "15.6\"", "17.3\""))
                            .refreshRate(refreshRate)
                            .camera(faker.options().option("12MP", "48MP", "50MP", "200MP"))
                            .frontCamera(faker.options().option("12MP", "16MP", "32MP"))
                            .battery(String.valueOf(faker.number().numberBetween(3000, 6000)) + "mAh");
                }

                // === INPUT & CONTROL CATEGORIES ===
                else if (List.of("Keyboards", "Mice").contains(categoryName)) {
                    if ("Keyboards".equals(categoryName)) {
                        String switchType = faker.options().option("Mechanical - Red Cherry MX", "Mechanical - Blue Kailh", "Optical", "Scissor");
                        String backlighting = faker.options().option("RGB", "White LED", "Single-color", "None");

                        Integer[] keyboardDpiOptions = {800, 1600, 3200};
                        Integer[] keyboardPollingRateOptions = {125, 500, 1000};

                        productBuilder
                                .switchType(switchType)
                                .backlighting(backlighting)
                                .programmableButtons(faker.bool().bool())
                                .dpi(keyboardDpiOptions[random.nextInt(keyboardDpiOptions.length)])
                                .pollingRate(keyboardPollingRateOptions[random.nextInt(keyboardPollingRateOptions.length)])
                                .batteryLife(faker.options().option("40 hours", "100 hours", "200 hours"));
                    }

                    if ("Mice".equals(categoryName)) {
                        Integer[] mouseDpiOptions = {800, 1600, 2400, 3200, 5000};
                        Integer[] mousePollingRateOptions = {125, 250, 500, 1000};

                        productBuilder
                                .dpi(mouseDpiOptions[random.nextInt(mouseDpiOptions.length)])
                                .pollingRate(mousePollingRateOptions[random.nextInt(mousePollingRateOptions.length)])
                                .programmableButtons(faker.bool().bool())
                                .batteryLife(faker.options().option("30 days", "60 days", "100 hours"));
                    }
                }

                // === OTHER CATEGORIES (no special attributes) ===
                else {
                    // No special attributes for other categories in the current Product entity
                }

                products.add(productBuilder.build());
            }
        }
        productRepo.saveAll(products);
        System.out.println(products.size() + " realistic products created with updated schema.");
    }

    private String injectRealisticValues(String template, String brand, String category) {
        // Use a copy to avoid modifying the original template in the map
        String result = template;

        // Category-specific naming logic
        // Use a loop that replaces one placeholder at a time for templates with multiple "%s"
        while (result.contains("%s")) {
            String replacement = "";

            switch (category) {
                case "Smartphones":
                    if ("Apple".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(13, 16)); // iPhone 13-16
                    } else if ("Samsung".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(21, 25)); // Galaxy S21-S25
                    } else if ("Google".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(7, 9)); // Pixel 7-9
                    } else if ("Sony".equals(brand)) {
                        replacement = faker.options().option("1 V", "5 V", "10 V"); // Xperia models
                    } else if ("OnePlus".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(10, 12));
                    } else if ("Xiaomi".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(12, 14));
                    } else {
                        replacement = String.valueOf(faker.number().numberBetween(9, 15));
                    }
                    break;

                case "Tablets":
                    if ("Apple".equals(brand)) {
                        replacement = faker.options().option("Air", "Pro 11", "Pro 12.9", "Mini", "10th Gen");
                    } else if ("Samsung".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(8, 9)); // Tab S8-S9
                    } else {
                        replacement = String.valueOf(faker.number().numberBetween(8, 12));
                    }
                    break;

                case "Laptops":
                    if ("Apple".equals(brand)) {
                        replacement = faker.options().option("Air M2", "Air M3", "Pro M2", "Pro M3", "14-inch", "16-inch");
                    } else if ("Dell".equals(brand) || "HP".equals(brand) || "Lenovo".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(13, 17)); // Screen sizes
                    } else if ("Razer".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(14, 18));
                    } else if ("Alienware".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(5, 8)); // e.g., R5, R6
                    } else {
                        replacement = faker.options().option("13", "14", "15", "16", "17");
                    }
                    break;

                case "Controllers":
                    if ("Sony".equals(brand)) {
                        replacement = faker.options().option("Edge", "Wireless", "Portal");
                    } else if ("Microsoft".equals(brand)) {
                        replacement = faker.options().option("Series X/S", "Elite Series 2", "Core", "Wireless");
                    } else if ("8BitDo".equals(brand)) {
                        replacement = faker.options().option("Pro 2", "Ultimate", "Zero 2", "SN30 Pro");
                    } else if ("Razer".equals(brand)) {
                        replacement = faker.options().option("Wolverine V2", "Kishi V2", "Raiju");
                    } else {
                        replacement = faker.options().option("Pro", "Elite", "Wireless", "Ultimate");
                    }
                    break;

                case "Keyboards":
                    if ("Logitech".equals(brand)) {
                        replacement = faker.options().option("MX Keys S", "G915 TKL", "G Pro X TKL", "Wave Keys");
                    } else if ("Razer".equals(brand)) {
                        replacement = faker.options().option("BlackWidow V4", "Huntsman V3", "Ornata V3");
                    } else if ("Corsair".equals(brand)) {
                        replacement = faker.options().option("K70", "K100", "K65");
                    } else if (Arrays.asList("Keychron", "Ducky").contains(brand)) {
                        replacement = faker.options().option("K2", "Q1", "V1", "One 3");
                    } else {
                        replacement = faker.options().option("Pro", "Elite", "Gaming", "Mechanical", "TKL");
                    }
                    break;

                case "Mice":
                    if ("Logitech".equals(brand)) {
                        replacement = faker.options().option("MX Master 3S", "G Pro X Superlight 2", "G502 X");
                    } else if ("Razer".equals(brand)) {
                        replacement = faker.options().option("DeathAdder V3 Pro", "Viper V2 Pro", "Basilisk V3 Pro");
                    } else if ("SteelSeries".equals(brand)) {
                        replacement = faker.options().option("Aerox 5", "Rival 3", "Sensei Ten");
                    } else {
                        replacement = faker.options().option("Pro", "Gaming", "Wireless", "Elite");
                    }
                    break;

                case "Handhelds":
                    if ("Steam".equals(brand)) {
                        replacement = faker.options().option("256GB", "512GB", "1TB");
                    } else if ("ASUS".equals(brand)) {
                        replacement = faker.options().option("Z1 Extreme", "Z1");
                    } else if ("Lenovo".equals(brand)) {
                        replacement = faker.options().option("512GB", "1TB");
                    } else {
                        replacement = faker.options().option("Pro", "OLED", "Ultimate", "Win 4");
                    }
                    break;

                default:
                    // Fallback for any other category
                    replacement = faker.options().option("Pro", "Max", "Ultra", "Elite", "Plus", "Standard Edition");
                    break;
            }

            // Replace only the first occurrence of "%s" to handle multiple placeholders correctly
            result = result.replaceFirst("%s", Matcher.quoteReplacement(replacement));
        }

        return result.trim();
    }

    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1).findAny().orElse(startEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440));
    }

    private void seedProductVariants() {
        System.out.println("Seeding realistic product variants based on categories...");
        List<Product> products = productRepo.findAll();
        if (products.isEmpty()) {
            System.out.println("No products found. Skipping variant seeding.");
            return;
        }

        List<ProductVariant> variants = new ArrayList<>();
        for (Product product : products) {
            String categoryName = product.getCategory().getName();
            List<ProductVariant> productVariants = createVariantsForCategory(product, categoryName);
            variants.addAll(productVariants);

            // Calculate total stock for the product
            int totalStockForProduct = productVariants.stream()
                    .mapToInt(ProductVariant::getStock)
                    .sum();
            product.setTotalStock(totalStockForProduct);
        }

        productRepo.saveAll(products);
        productVariantRepo.saveAll(variants);
        System.out.println(variants.size() + " realistic product variants created.");
    }

    private List<ProductVariant> createVariantsForCategory(Product product, String categoryName) {
        List<ProductVariant> variants = new ArrayList<>();
        String brand = product.getBrand();

        switch (categoryName) {
            case "Smartphones":
                variants.addAll(createSmartphoneVariants(product, brand));
                break;
            case "Tablets":
                variants.addAll(createTabletVariants(product, brand));
                break;
            case "Laptops":
                variants.addAll(createLaptopVariants(product, brand));
                break;
            case "Handhelds":
                variants.addAll(createHandheldVariants(product, brand));
                break;
            case "Keyboards":
                variants.addAll(createKeyboardVariants(product, brand));
                break;
            case "Mice":
                variants.addAll(createMouseVariants(product, brand));
                break;
            case "Controllers":
                variants.addAll(createControllerVariants(product, brand));
                break;
            default:
                // Fallback for any other categories
                variants.addAll(createGenericVariants(product));
                break;
        }

        return variants;
    }

    private List<ProductVariant> createSmartphoneVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Storage options based on brand
        List<String> storageOptions;
        if ("Apple".equals(brand)) {
            storageOptions = Arrays.asList("128GB", "256GB", "512GB", "1TB");
        } else {
            storageOptions = Arrays.asList("128GB", "256GB", "512GB");
        }

        // Restricted smartphone colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Create variants for each storage-color combination
        for (String storage : storageOptions) {
            // Not all colors available for all storage options (realistic)
            int colorCount = Math.min(random.nextInt(4) + 2, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(50) + 5; // Lower stock for phones
                variants.add(ProductVariant.builder()
                        .size(storage) // Using size field for storage
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + storage + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createTabletVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Storage options for tablets
        List<String> storageOptions;
        if ("Apple".equals(brand)) {
            storageOptions = Arrays.asList("64GB", "256GB", "512GB", "1TB", "2TB");
        } else if ("Microsoft".equals(brand)) {
            storageOptions = Arrays.asList("128GB", "256GB", "512GB", "1TB");
        } else {
            storageOptions = Arrays.asList("64GB", "128GB", "256GB", "512GB");
        }

        // Restricted tablet colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        for (String storage : storageOptions) {
            int colorCount = Math.min(random.nextInt(3) + 2, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(30) + 8;
                variants.add(ProductVariant.builder()
                        .size(storage)
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + storage + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createLaptopVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // RAM/Storage configurations
        List<String> configurations;
        if ("Apple".equals(brand)) {
            configurations = Arrays.asList(
                "8GB/256GB", "8GB/512GB", "16GB/512GB", "16GB/1TB", "32GB/1TB", "32GB/2TB"
            );
        } else {
            configurations = Arrays.asList(
                "8GB/256GB", "16GB/512GB", "16GB/1TB", "32GB/512GB", "32GB/1TB"
            );
        }

        // Restricted laptop colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        for (String config : configurations) {
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(20) + 3; // Lower stock for laptops
                variants.add(ProductVariant.builder()
                        .size(config) // Using size field for RAM/Storage config
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + config.replace("/", "-") + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createHandheldVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Storage options for gaming handhelds
        List<String> storageOptions = Arrays.asList("64GB", "256GB", "512GB", "1TB");

        // Restricted handheld colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        for (String storage : storageOptions) {
            int colorCount = Math.min(random.nextInt(2) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(15) + 2;
                variants.add(ProductVariant.builder()
                        .size(storage)
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + storage + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createKeyboardVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Switch types for mechanical keyboards
        List<String> switchTypes = Arrays.asList(
            "Red Switch", "Blue Switch", "Brown Switch", "Black Switch",
            "Silver Switch", "Tactile", "Linear", "Clicky"
        );

        // Restricted keyboard colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Some keyboards come in different switch types
        boolean hasSwitchVariants = product.getName().toLowerCase().contains("mechanical") ||
                                   Arrays.asList("Razer", "Corsair", "Logitech", "SteelSeries").contains(brand);

        if (hasSwitchVariants) {
            int switchCount = Math.min(random.nextInt(3) + 1, switchTypes.size());
            List<String> selectedSwitches = getRandomSubset(switchTypes, switchCount);

            for (String switchType : selectedSwitches) {
                int colorCount = Math.min(random.nextInt(2) + 1, colors.size());
                List<String> selectedColors = getRandomSubset(colors, colorCount);

                for (String color : selectedColors) {
                    int stock = random.nextInt(25) + 5;
                    variants.add(ProductVariant.builder()
                            .size(switchType) // Using size field for switch type
                            .color(color)
                            .stock(stock)
                            .sku(generateSKU(product.getName() + "-" + switchType.replace(" ", "") + "-" + color))
                            .product(product)
                            .build());
                }
            }
        } else {
            // Non-mechanical keyboards - just color variants
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(30) + 10;
                variants.add(ProductVariant.builder()
                        .size("Standard")
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createMouseVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Restricted mouse colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Some gaming mice have different DPI variants
        boolean hasGamingVariants = product.getName().toLowerCase().contains("gaming") ||
                                   Arrays.asList("Razer", "Logitech", "Corsair", "SteelSeries").contains(brand);

        if (hasGamingVariants) {
            List<String> dpiVariants = Arrays.asList("Standard DPI", "High DPI", "Pro DPI");

            for (String dpi : dpiVariants) {
                int colorCount = Math.min(random.nextInt(2) + 1, colors.size());
                List<String> selectedColors = getRandomSubset(colors, colorCount);

                for (String color : selectedColors) {
                    int stock = random.nextInt(20) + 5;
                    variants.add(ProductVariant.builder()
                            .size(dpi)
                            .color(color)
                            .stock(stock)
                            .sku(generateSKU(product.getName() + "-" + dpi.replace(" ", "") + "-" + color))
                            .product(product)
                            .build());
                }
            }
        } else {
            // Regular mice - just color variants
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(25) + 8;
                variants.add(ProductVariant.builder()
                        .size("Standard")
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createControllerVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Restricted controller colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Special editions for some brands
        List<String> editions = Arrays.asList("Standard", "Elite", "Pro", "Limited Edition");

        for (String edition : editions.subList(0, random.nextInt(editions.size()) + 1)) {
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(20) + 5;
                variants.add(ProductVariant.builder()
                        .size(edition)
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + edition.replace(" ", "") + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createGenericVariants(Product product) {
        List<ProductVariant> variants = new ArrayList<>();

        // Restricted generic colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");
        int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
        List<String> selectedColors = getRandomSubset(colors, colorCount);

        for (String color : selectedColors) {
            int stock = random.nextInt(30) + 10;
            variants.add(ProductVariant.builder()
                    .size("Standard")
                    .color(color)
                    .stock(stock)
                    .sku(generateSKU(product.getName() + "-" + color))
                    .product(product)
                    .build());
        }

        return variants;
    }

    private <T> List<T> getRandomSubset(List<T> list, int size) {
        size = Math.min(size, list.size());
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, random);
        return copy.subList(0, size);
    }

    private String generateSKU(String productInfo) {
        // Remove special characters and spaces, then add random suffix
        String cleanInfo = productInfo.replaceAll("[^a-zA-Z0-9-]", "")
                .replaceAll("\\s+", "-")
                .toUpperCase();

        // Limit length and add random suffix for uniqueness
        if (cleanInfo.length() > 20) {
            cleanInfo = cleanInfo.substring(0, 20);
        }

        return cleanInfo + "-" + faker.number().numberBetween(1000, 9999);
    }
}

