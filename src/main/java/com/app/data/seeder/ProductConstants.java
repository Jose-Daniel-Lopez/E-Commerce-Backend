package com.app.data.seeder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductConstants {

    public static final Map<String, List<String>> CATEGORY_BRANDS = createCategoryBrandsMap();
    public static final Map<String, List<String>> BRAND_NAMING_RULES = createBrandNamingRules();

    private static Map<String, List<String>> createCategoryBrandsMap() {
        Map<String, List<String>> map = new HashMap<>();

        // === MOBILE & COMPUTE ===
        map.put("Smartphones", Arrays.asList(
                "Apple", "Samsung", "Google", "Xiaomi", "OnePlus", "Sony", "Motorola", "Oppo", "Realme", "Asus", "Nokia"
        ));

        map.put("Tablets", Arrays.asList(
                "Apple", "Samsung", "Microsoft", "Lenovo", "Xiaomi", "Huawei"
        ));

        map.put("Laptops", Arrays.asList(
                "Apple", "Dell", "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer", "Asus", "Alienware", "LG"
        ));

        map.put("Handhelds", Arrays.asList(
                "ASUS", "AYANEO", "GPD", "Logitech", "Razer", "Steam", "Lenovo"
        ));

        // === INPUT & CONTROL ===
        map.put("Keyboards", Arrays.asList(
                "Logitech", "Corsair", "Razer", "SteelSeries", "HyperX", "Ducky", "Keychron", "Epomaker", "Kinesis", "Microsoft", "Apple", "Das Keyboard"
        ));

        map.put("Mice", Arrays.asList(
                "Logitech", "Razer", "Corsair", "SteelSeries", "Microsoft", "Apple", "Finalmouse", "Glorious", "Zowie"
        ));

        map.put("Controllers", Arrays.asList(
                "Sony", "Microsoft", "Nintendo", "8BitDo", "Razer", "Logitech", "Astro", "DualShock", "Scuf", "PowerA"
        ));

        return map;
    }

    private static Map<String, List<String>> createBrandNamingRules() {
        Map<String, List<String>> rules = new HashMap<>();

        // === MOBILE & COMPUTE ===
        rules.put("Apple", Arrays.asList(
                "iPhone 13", "iPhone 13 Pro", "iPhone 13 Pro Max", "iPhone 13 Mini",
                "iPhone 14", "iPhone 14 Pro", "iPhone 14 Pro Max", "iPhone 14 Plus",
                "iPhone 15", "iPhone 15 Pro", "iPhone 15 Pro Max", "iPhone 15 Plus",
                "iPhone SE (3rd generation)",
                "MacBook Air 13-inch (M2)", "MacBook Air 15-inch (M2)", "MacBook Air 13-inch (M3)",
                "MacBook Pro 14-inch (M3)", "MacBook Pro 14-inch (M3 Pro)", "MacBook Pro 14-inch (M3 Max)",
                "MacBook Pro 16-inch (M3 Pro)", "MacBook Pro 16-inch (M3 Max)",
                "iPad (10th generation)", "iPad mini (6th generation)",
                "iPad Air (5th generation)",
                "iPad Pro 11-inch (4th generation)", "iPad Pro 12.9-inch (6th generation)"
        ));

        rules.put("Samsung", Arrays.asList(
                "Galaxy S24", "Galaxy S24+", "Galaxy S24 Ultra",
                "Galaxy S23", "Galaxy S23+", "Galaxy S23 Ultra", "Galaxy S23 FE",
                "Galaxy Z Fold5", "Galaxy Z Flip5",
                "Galaxy A54 5G", "Galaxy A34 5G",
                "Galaxy Tab S9", "Galaxy Tab S9+", "Galaxy Tab S9 Ultra", "Galaxy Tab S9 FE"
        ));

        rules.put("Google", Arrays.asList(
                "Pixel 8", "Pixel 8 Pro",
                "Pixel 7", "Pixel 7 Pro", "Pixel 7a",
                "Pixel Fold",
                "Pixel Tablet"
        ));

        rules.put("Xiaomi", Arrays.asList(
                "Xiaomi 14", "Xiaomi 14 Pro", "Xiaomi 14 Ultra",
                "Xiaomi 13", "Xiaomi 13 Pro", "Xiaomi 13 Ultra", "Xiaomi 13T",
                "Redmi Note 13 Pro", "Redmi Note 12",
                "Poco F5 Pro", "Poco X6 Pro"
        ));

        rules.put("OnePlus", Arrays.asList(
                "OnePlus 12", "OnePlus 12R",
                "OnePlus 11",
                "OnePlus Open",
                "OnePlus Nord 3", "OnePlus Nord CE 3 Lite"
        ));

        rules.put("Microsoft", Arrays.asList(
                "Surface Pro 9", "Surface Pro 10 for Business",
                "Surface Laptop 5", "Surface Laptop Studio 2", "Surface Laptop Go 3",
                "Surface Go 4 for Business",
                "Xbox Wireless Controller", "Xbox Elite Wireless Controller Series 2", "Xbox Adaptive Controller",
                "Microsoft Sculpt Ergonomic Desktop", "Microsoft Ergonomic Keyboard", "Microsoft Surface Mouse", "Microsoft Arc Mouse"
        ));

        rules.put("Dell", Arrays.asList(
                "XPS 13", "XPS 14", "XPS 15", "XPS 16", "XPS 17",
                "Inspiron 15", "Inspiron 16",
                "Latitude 7440", "Latitude 9440",
                "Alienware m16", "Alienware m18", "Alienware x16"
        ));

        rules.put("HP", Arrays.asList(
                "Spectre x360 14", "Spectre x360 16",
                "Envy x360 15",
                "Pavilion Aero 13", "Pavilion Plus 14",
                "Omen 16", "Omen 17",
                "EliteBook 840 G10"
        ));

        rules.put("Lenovo", Arrays.asList(
                "ThinkPad X1 Carbon Gen 11", "ThinkPad X1 Yoga Gen 8",
                "Legion Pro 7i Gen 8", "Legion Slim 5 Gen 8",
                "IdeaPad Slim 5", "IdeaPad Pro 5i",
                "Yoga 9i 2-in-1 (14″, Gen 8)", "Yoga 7i (16″, Gen 8)",
                "Legion Go"
        ));

        rules.put("ASUS", Arrays.asList(
                "ROG Zephyrus G14", "ROG Zephyrus G16",
                "ROG Flow X13", "ROG Flow Z13",
                "TUF Gaming A15", "TUF Gaming F17",
                "Vivobook Pro 16X OLED", "Vivobook S 15 OLED",
                "Zenbook 14 OLED", "Zenbook Pro Duo 14 OLED",
                "ROG Ally"
        ));

        rules.put("Acer", Arrays.asList(
                "Predator Helios 16", "Predator Helios Neo 16",
                "Nitro 17", "Nitro V 15",
                "Swift Go 14", "Swift X 14",
                "Aspire 5", "Aspire 7"
        ));

        rules.put("MSI", Arrays.asList(
                "Titan GT77 HX", "Raider GE78 HX",
                "Stealth 16 Studio", "Stealth 17 Studio",
                "Cyborg 15", "Thin GF63"
        ));

        rules.put("Razer", Arrays.asList(
                "Blade 14", "Blade 15", "Blade 16", "Blade 18",
                "DeathAdder V3 Pro", "Viper V2 Pro", "Basilisk V3 Pro", "Naga V2 Pro",
                "BlackWidow V4 Pro", "Huntsman V3 Pro", "Ornata V3",
                "Wolverine V2 Chroma", "Kishi V2 Pro"
        ));

        rules.put("Alienware", Arrays.asList(
                "m16 R2", "m18 R2", "x16 R2"
        ));

        rules.put("LG", Arrays.asList(
                "Gram 14", "Gram 16", "Gram 17", "Gram SuperSlim",
                "UltraGear 27GR95QE-B", "UltraGear 32GS95UE-B"
        ));

        // === HANDHELDS ===
        rules.put("AYANEO", Arrays.asList(
                "AYANEO 2S", "AYANEO GEEK 1S", "AYANEO KUN", "AYANEO SLIDE"
        ));

        rules.put("GPD", Arrays.asList(
                "GPD WIN 4", "GPD WIN Max 2", "GPD Pocket 3"
        ));

        rules.put("Steam", Arrays.asList(
                "Steam Deck LCD 64GB", "Steam Deck LCD 512GB", "Steam Deck OLED 512GB", "Steam Deck OLED 1TB"
        ));

        // === INPUT & CONTROL ===
        rules.put("Logitech", Arrays.asList(
                "MX Master 3S", "MX Anywhere 3S", "MX Vertical", "G Pro X Superlight 2", "G502 X PLUS",
                "MX Keys S", "MX Mechanical", "Wave Keys", "G915 TKL", "PRO X TKL LIGHTSPEED",
                "G Cloud Gaming Handheld"
        ));

        rules.put("Corsair", Arrays.asList(
                "K100 AIR Wireless", "K70 MAX", "K65 PRO MINI",
                "M75 AIR", "DARKSTAR WIRELESS", "SCIMITAR ELITE WIRELESS"
        ));

        rules.put("SteelSeries", Arrays.asList(
                "Apex Pro TKL", "Apex 7 TKL",
                "Aerox 9 Wireless", "Rival 5", "Sensei Ten"
        ));

        rules.put("HyperX", Arrays.asList(
                "Alloy Origins", "Alloy Elite 2",
                "Pulsefire Haste 2", "Pulsefire Dart"
        ));

        rules.put("Keychron", Arrays.asList(
                "Q1 Pro", "Q2 Pro", "K2 Pro", "V1 Max"
        ));

        rules.put("Ducky", Arrays.asList(
                "One 3", "Shine 7"
        ));

        rules.put("Kinesis", Arrays.asList(
                "Advantage360", "Freestyle Edge RGB"
        ));

        rules.put("Epomaker", Arrays.asList(
                "TH80 Pro", "EK68"
        ));

        rules.put("8BitDo", Arrays.asList(
                "Ultimate Controller", "Pro 2 Controller", "SN30 Pro Controller"
        ));

        rules.put("Sony", Arrays.asList(
                "Xperia 1 V", "Xperia 5 V",
                "DualSense Wireless Controller", "DualSense Edge Wireless Controller"
        ));

        // Fallback for any brand not explicitly listed
        rules.put("DEFAULT", Arrays.asList(
                "%s %s",
                "%s %s Edition",
                "%s Model %s",
                "%s Series %s"
        ));

        return rules;
    }

    public static String getCategoryForProduct(String productName) {
        String lowerCaseName = productName.toLowerCase();

        // Smartphones
        if (lowerCaseName.contains("iphone") || lowerCaseName.contains("galaxy s") || lowerCaseName.contains("pixel") && !lowerCaseName.contains("tablet") || lowerCaseName.contains("xperia") || lowerCaseName.contains("oneplus") || lowerCaseName.contains("xiaomi") || lowerCaseName.contains("redmi") || lowerCaseName.contains("poco")) {
            return "Smartphones";
        }
        // Tablets
        if (lowerCaseName.contains("ipad") || lowerCaseName.contains("galaxy tab") || lowerCaseName.contains("pixel tablet") || lowerCaseName.contains("surface go") || lowerCaseName.contains("surface pro")) {
            return "Tablets";
        }
        // Laptops
        if (lowerCaseName.contains("macbook") || lowerCaseName.contains("surface laptop") || lowerCaseName.contains("xps") || lowerCaseName.contains("inspiron") || lowerCaseName.contains("latitude") || lowerCaseName.contains("spectre") || lowerCaseName.contains("envy") || lowerCaseName.contains("pavilion") || lowerCaseName.contains("omen") || lowerCaseName.contains("elitebook") || lowerCaseName.contains("thinkpad") || lowerCaseName.contains("legion") && !lowerCaseName.contains("go") || lowerCaseName.contains("ideapad") || lowerCaseName.contains("yoga") || lowerCaseName.contains("zephyrus") || lowerCaseName.contains("flow") && !lowerCaseName.contains("z13") || lowerCaseName.contains("tuf gaming") || lowerCaseName.contains("vivobook") || lowerCaseName.contains("zenbook") || lowerCaseName.contains("predator") || lowerCaseName.contains("nitro") || lowerCaseName.contains("swift") || lowerCaseName.contains("aspire") || lowerCaseName.contains("titan") || lowerCaseName.contains("raider") || lowerCaseName.contains("stealth") || lowerCaseName.contains("cyborg") || lowerCaseName.contains("thin gf") || lowerCaseName.contains("blade") || lowerCaseName.contains("alienware") || lowerCaseName.contains("gram")) {
            return "Laptops";
        }
        // Handhelds
        if (lowerCaseName.contains("rog ally") || lowerCaseName.contains("legion go") || lowerCaseName.contains("steam deck") || lowerCaseName.contains("ayaneo") || lowerCaseName.contains("gpd win") || lowerCaseName.contains("g cloud")) {
            return "Handhelds";
        }
        // Keyboards
        if (lowerCaseName.contains("keyboard") || lowerCaseName.contains("keys") || lowerCaseName.contains("blackwidow") || lowerCaseName.contains("huntsman") || lowerCaseName.contains("ornata") || lowerCaseName.contains("k100") || lowerCaseName.contains("k70") || lowerCaseName.contains("k65") || lowerCaseName.contains("apex") || lowerCaseName.contains("alloy") || lowerCaseName.contains("keychron") || lowerCaseName.contains("ducky") || lowerCaseName.contains("kinesis") || lowerCaseName.contains("epomaker") || lowerCaseName.contains("th80")) {
            return "Keyboards";
        }
        // Mice
        if (lowerCaseName.contains("mouse") || lowerCaseName.contains("deathadder") || lowerCaseName.contains("viper") || lowerCaseName.contains("basilisk") || lowerCaseName.contains("naga") || lowerCaseName.contains("m75") || lowerCaseName.contains("darkstar") || lowerCaseName.contains("scimitar") || lowerCaseName.contains("aerox") || lowerCaseName.contains("rival") || lowerCaseName.contains("sensei") || lowerCaseName.contains("pulsefire")) {
            return "Mice";
        }
        // Controllers
        if (lowerCaseName.contains("controller") || lowerCaseName.contains("dualsense") || lowerCaseName.contains("wolverine") || lowerCaseName.contains("kishi") || lowerCaseName.contains("8bitdo")) {
            return "Controllers";
        }

        return null; // No specific category found
    }

    public static boolean isValidBrandCategory(String brand, String category) {
        Map<String, List<String>> validCategories = new HashMap<>();

        validCategories.put("Apple", Arrays.asList("Smartphones", "Tablets", "Laptops", "Keyboards", "Mice"));
        validCategories.put("Samsung", Arrays.asList("Smartphones", "Tablets"));
        validCategories.put("Google", Arrays.asList("Smartphones", "Tablets"));
        validCategories.put("Sony", Arrays.asList("Smartphones", "Laptops", "Controllers"));
        validCategories.put("Microsoft", Arrays.asList("Laptops", "Tablets", "Keyboards", "Mice", "Controllers"));
        validCategories.put("Xiaomi", Arrays.asList("Smartphones", "Tablets"));
        validCategories.put("ASUS", Arrays.asList("Laptops", "Handhelds"));
        validCategories.put("Razer", Arrays.asList("Laptops", "Handhelds", "Keyboards", "Mice", "Controllers"));
        validCategories.put("Logitech", Arrays.asList("Handhelds", "Keyboards", "Mice", "Controllers"));
        validCategories.put("Corsair", Arrays.asList("Keyboards", "Mice"));
        validCategories.put("SteelSeries", Arrays.asList("Keyboards", "Mice"));
        validCategories.put("HyperX", Arrays.asList("Keyboards", "Mice"));
        validCategories.put("Ducky", List.of("Keyboards"));
        validCategories.put("Keychron", List.of("Keyboards"));
        validCategories.put("Epomaker", List.of("Keyboards"));
        validCategories.put("8BitDo", List.of("Controllers"));
        validCategories.put("AYANEO", List.of("Handhelds"));
        validCategories.put("GPD", List.of("Handhelds"));
        validCategories.put("Steam", List.of("Handhelds"));

        List<String> allowed = validCategories.getOrDefault(brand, null);
        if (allowed == null) return true;
        return allowed.contains(category);
    }
}
