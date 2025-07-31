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
                "iPhone %s", "iPhone %s Pro", "iPhone %s Pro Max",
                "MacBook %s", "MacBook %s Air", "MacBook %s Pro",
                "iPad %s", "iPad %s Pro", "iPad %s Air", "iPad %s Mini"
        ));

        rules.put("Samsung", Arrays.asList(
                "Galaxy S%s", "Galaxy S%s Ultra", "Galaxy S%s+", "Galaxy Z %s",
                "Galaxy Note %s", "Galaxy Tab S%s"
        ));

        rules.put("Google", Arrays.asList(
                "Pixel %s", "Pixel %s Pro", "Pixel Tablet %s"
        ));

        rules.put("Xiaomi", Arrays.asList(
                "Mi %s", "Redmi %s", "Redmi Note %s", "Poco %s", "Xiaomi %s"
        ));

        rules.put("OnePlus", Arrays.asList(
                "OnePlus %s", "OnePlus %s Pro", "OnePlus Nord %s", "OnePlus %sT"
        ));

        rules.put("Microsoft", Arrays.asList(
                "Surface %s", "Surface Pro %s", "Surface Laptop %s", "Surface Book %s",
                "Xbox Wireless Controller %s", "Elite %s", "Adaptive Controller",
                "Sculpt %s", "Ergonomic Keyboard %s", "Surface Mouse %s", "Arc Mouse %s"
        ));

        rules.put("Dell", Arrays.asList(
                "XPS %s", "Inspiron %s", "Latitude %s", "Alienware %s", "G Series %s"
        ));

        rules.put("HP", Arrays.asList(
                "Spectre %s", "Envy %s", "Pavilion %s", "Omen %s", "EliteBook %s"
        ));

        rules.put("Lenovo", Arrays.asList(
                "ThinkPad %s", "Legion %s", "IdeaPad %s", "Yoga %s", "Flex %s", "Legion Go %s"
        ));

        rules.put("ASUS", Arrays.asList(
                "ROG Zephyrus %s", "ROG Flow %s", "TUF Gaming %s", "Vivobook %s", "Zenbook %s", "ROG Ally %s"
        ));

        rules.put("Acer", Arrays.asList(
                "Predator %s", "Nitro %s", "Swift %s", "Aspire %s"
        ));

        rules.put("MSI", Arrays.asList(
                "GS %s", "GP %s", "Stealth %s", "Alpha %s"
        ));

        rules.put("Razer", Arrays.asList(
                "Blade %s", "Blade Stealth %s", "Blade Pro %s",
                "DeathAdder %s", "Viper %s", "Basilisk %s", "Naga %s",
                "BlackWidow %s", "Huntsman %s", "Ornata %s",
                "Wolverine %s", "Kishi %s"
        ));

        rules.put("Alienware", Arrays.asList(
                "m15 R%s", "m16 R%s", "x14 R%s", "x16 R%s"
        ));

        rules.put("LG", Arrays.asList(
                "Gram %s", "UltraFine %s", "UltraGear %s"
        ));

        // === HANDHELDS ===
        rules.put("AYANEO", Arrays.asList(
                "AYANEO %s", "AYANEO %s Pro", "AYANEO Geek %s", "AYANEO Slide %s"
        ));

        rules.put("GPD", Arrays.asList(
                "GPD Win %s", "GPD Pocket %s", "GPD MicroPC %s"
        ));

        rules.put("Steam", Arrays.asList(
                "Steam Deck %s", "Steam Deck OLED %s"
        ));

        // === INPUT & CONTROL ===
        rules.put("Logitech", Arrays.asList(
                "MX %s", "G %s", "Z %s", "PRO %s", "Craft %s", "POP %s", "G Cloud %s"
        ));

        rules.put("Corsair", Arrays.asList(
                "K%s", "K95 %s", "Stratix %s",
                "M%s", "Dark Core %s", "Sabre %s"
        ));

        rules.put("SteelSeries", Arrays.asList(
                "Apex %s", "Apex Pro %s", "Apex 7 %s",
                "Rival %s", "Sensei %s", "Aerox %s"
        ));

        rules.put("HyperX", Arrays.asList(
                "Alloy %s", "Pulsefire %s", "Cloud %s"
        ));

        rules.put("Keychron", Arrays.asList(
                "K%s", "Q%s", "C%s", "V%s"
        ));

        rules.put("Ducky", Arrays.asList(
                "Shine %s", "One %s", "King %s"
        ));

        rules.put("Kinesis", Arrays.asList(
                "Freestyle %s", "Advantage %s", "Ergo %s"
        ));

        rules.put("Epomaker", Arrays.asList(
                "TH80 %s", "ERGO42 %s", "AJ60 %s"
        ));

        rules.put("8BitDo", Arrays.asList(
                "Ultimate %s", "Pro %s", "Zero %s", "SN30 %s"
        ));

        rules.put("Sony", Arrays.asList(
                "Xperia %s", "Xperia %s Pro",
                "DualShock %s", "DualSense %s"
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

