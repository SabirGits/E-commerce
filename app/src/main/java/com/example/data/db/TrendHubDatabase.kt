package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    ProductEntity::class,
    CategoryEntity::class,
    CartItemEntity::class,
    WishlistItemEntity::class,
    OrderEntity::class,
    CouponEntity::class,
    AdBannerEntity::class,
    ReviewEntity::class,
    UserAddressEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class TrendHubDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun categoryDao(): CategoryDao
  abstract fun cartDao(): CartDao
  abstract fun wishlistDao(): WishlistDao
  abstract fun orderDao(): OrderDao
  abstract fun couponDao(): CouponDao
  abstract fun adBannerDao(): AdBannerDao
  abstract fun reviewDao(): ReviewDao
  abstract fun addressDao(): AddressDao

  companion object {
    @Volatile
    private var INSTANCE: TrendHubDatabase? = null

    fun getDatabase(context: Context): TrendHubDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TrendHubDatabase::class.java,
          "trendhub_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    fun seedDatabaseIfEmpty(db: TrendHubDatabase) {
      CoroutineScope(Dispatchers.IO).launch {
        if (db.productDao().getCount() == 0) {
          db.categoryDao().insertAll(SeedData.categories)
          db.productDao().insertAll(SeedData.products)
          db.couponDao().insertAll(SeedData.coupons)
          db.adBannerDao().insertAll(SeedData.banners)
          db.reviewDao().insertAll(SeedData.reviews)
        }
      }
    }
  }
}

object SeedData {
  val categories = listOf(
    CategoryEntity(1, "Smartphones", "smartphone", 8),
    CategoryEntity(2, "Laptops & PC", "laptop", 5),
    CategoryEntity(3, "Audio & Sound", "headphones", 4),
    CategoryEntity(4, "Cameras & Drones", "camera", 3),
    CategoryEntity(5, "Gaming & Toys", "sports_esports", 6),
    CategoryEntity(6, "Wearables", "watch", 3),
    CategoryEntity(7, "Displays & TV", "tv", 3),
    CategoryEntity(8, "Accessories", "cable", 8)
  )

  val products = listOf(
    // 1. iPhone
    ProductEntity(
      id = 1,
      name = "iPhone 16 Pro Max 1TB",
      brand = "Apple",
      category = "Smartphones",
      price = 1499.0,
      originalPrice = 1599.0,
      rating = 4.9,
      reviewsCount = 342,
      description = "Experience the pinnacle of titanium craftsmanship with the Apple A18 Pro chip, 48MP Fusion Camera system, and next-gen Spatial Audio recording. Features an ultra-bright Super Retina XDR OLED display with ProMotion 120Hz.",
      specs = "Display: 6.9 inch OLED 120Hz\nProcessor: Apple A18 Pro 3nm\nCamera: 48MP Main + 48MP Ultra-Wide + 12MP 5x Telephoto\nBattery: 4685 mAh, MagSafe wireless charging\nMaterials: Grade 5 Titanium Chassis",
      imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&q=80",
      isTrending = true,
      isBestSeller = true,
      isFlashSale = false,
      isDealOfDay = true,
      stock = 15,
      discountPercentage = 6,
      modelType = "PHONE"
    ),
    // 2. Samsung
    ProductEntity(
      id = 2,
      name = "Samsung Galaxy S24 Ultra AI",
      brand = "Samsung",
      category = "Smartphones",
      price = 1299.0,
      originalPrice = 1419.0,
      rating = 4.8,
      reviewsCount = 280,
      description = "Galaxy AI is here. Unleash new levels of creativity, productivity, and possibility with titanium armor frame, integrated S-Pen, and a massive 200MP Quad Tele System sensor suite.",
      specs = "Display: 6.8 inch Dynamic AMOLED 2X 120Hz\nProcessor: Snapdragon 8 Gen 3 for Galaxy\nCamera: 200MP + 50MP 5x + 10MP 3x + 12MP Ultra-Wide\nS-Pen: Built-in active stylus with Bluetooth\nBattery: 5000 mAh 45W Fast Charging",
      imageUrl = "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&q=80",
      isTrending = true,
      isBestSeller = true,
      isFlashSale = true,
      isDealOfDay = false,
      stock = 24,
      discountPercentage = 8,
      modelType = "PHONE"
    ),
    // 3. Nothing Phone
    ProductEntity(
      id = 3,
      name = "Nothing Phone (2a) Plus",
      brand = "Nothing",
      category = "Smartphones",
      price = 449.0,
      originalPrice = 499.0,
      rating = 4.7,
      reviewsCount = 195,
      description = "Designed to make tech fun again. Featuring the signature custom Glyph Interface light patterns on a transparent back shell, MediaTek Dimensity 7350 Pro processor, and dual 50MP cameras.",
      specs = "Display: 6.7 inch Flexible AMOLED 120Hz\nGlyph: 26 individually addressable LED zones\nProcessor: MediaTek Dimensity 7350 Pro\nCamera: 50MP Main + 50MP Ultra-Wide\nOS: Nothing OS 2.6 clean experience",
      imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&q=80",
      isTrending = true,
      isNewArrival = true,
      stock = 30,
      discountPercentage = 10,
      modelType = "PHONE"
    ),
    // 4. Google Pixel
    ProductEntity(
      id = 4,
      name = "Google Pixel 9 Pro XL",
      brand = "Google",
      category = "Smartphones",
      price = 1099.0,
      originalPrice = 1199.0,
      rating = 4.8,
      reviewsCount = 210,
      description = "The smartest smartphone built by Google. Engineered with the Google Tensor G4 chip, Gemini Nano AI integration, and the industry's most advanced computational photography suite.",
      specs = "Display: 6.8 inch Super Actua OLED 3000 nits\nProcessor: Google Tensor G4 with Titan M2 security\nAI: Gemini Live and Magic Editor built-in\nCamera: 50MP Octa PD + 48MP 5x Telephoto + 48MP UW\nUpdates: 7 years of OS and security updates",
      imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&q=80",
      isTrending = true,
      stock = 18,
      discountPercentage = 8,
      modelType = "PHONE"
    ),
    // 5. Gaming Phone
    ProductEntity(
      id = 5,
      name = "ROG Phone 8 Pro Cyber Edition",
      brand = "ASUS ROG",
      category = "Gaming & Toys",
      price = 1199.0,
      originalPrice = 1349.0,
      rating = 4.9,
      reviewsCount = 156,
      description = "Beyond gaming. Unrivaled speed meets cyberpunk aesthetics with Snapdragon 8 Gen 3, 24GB LPDDR5X RAM, 165Hz AMOLED screen, AirTrigger shoulder buttons, and AeroActive cooling fan.",
      specs = "Display: 6.78 inch LTPO AMOLED 165Hz\nRAM: 24GB LPDDR5X | ROM: 1TB UFS 4.0\nCooling: GameCool 8 Conductor thermal system\nAudio: Dirac Virtuo spatial audio headphone jack\nLighting: AniMe Vision 341 mini-LED display on back",
      imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&q=80",
      isTrending = true,
      isFlashSale = true,
      stock = 12,
      discountPercentage = 11,
      modelType = "PHONE"
    ),
    // 6. Phone Covers
    ProductEntity(
      id = 6,
      name = "Cyberpunk Carbon Armor Phone Cover",
      brand = "Spigen Cyber",
      category = "Accessories",
      price = 39.99,
      originalPrice = 59.99,
      rating = 4.6,
      reviewsCount = 420,
      description = "Military-grade drop protection with genuine Kevlar carbon fiber weave and CNC anodized aluminum camera guard. Built-in N52 magnetic ring for ultra-fast MagSafe connectivity.",
      specs = "Material: Aramid Carbon Fiber + TPU bumper\nDrop Protection: Certified 15ft military drop test\nCompatibility: iPhone 16 Pro Max / S24 Ultra / Pixel 9\nWeight: Ultra-lightweight 28 grams\nMagnets: N52 Neodymium array",
      imageUrl = "https://images.unsplash.com/photo-1603313011101-320f26a4f6f6?w=600&q=80",
      isBestSeller = true,
      stock = 85,
      discountPercentage = 33,
      modelType = "ACCESSORY"
    ),
    // 7. AirPods
    ProductEntity(
      id = 7,
      name = "AirPods Pro (2nd Gen) MagSafe USB-C",
      brand = "Apple",
      category = "Audio & Sound",
      price = 249.0,
      originalPrice = 279.0,
      rating = 4.9,
      reviewsCount = 890,
      description = "Re-engineered for 2x more Active Noise Cancellation. Adaptive Audio dynamically blends noise cancellation and transparency modes based on your surroundings. H2 acoustic chip.",
      specs = "Chip: Apple H2 Headphone Chip + U1 charging case\nNoise Cancellation: 2x Active Noise Cancellation\nBattery: 6 hours listening time, 30 hours with case\nAudio: Personalized Spatial Audio with dynamic head tracking\nWater Resistance: IP54 dust, sweat, and water resistant",
      imageUrl = "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=600&q=80",
      isBestSeller = true,
      isDealOfDay = true,
      stock = 40,
      discountPercentage = 10,
      modelType = "AUDIO"
    ),
    // 8. Bluetooth Speakers
    ProductEntity(
      id = 8,
      name = "JBL Pulse 5 RGB Party Speaker",
      brand = "JBL",
      category = "Audio & Sound",
      price = 199.99,
      originalPrice = 249.99,
      rating = 4.8,
      reviewsCount = 310,
      description = "Illuminate your music with eye-catching 360-degree LED light show that syncs to the beat of your songs. Delivers bold JBL Original Pro Sound in all directions with deep bass.",
      specs = "Sound: 360-degree JBL Pro Sound with separate tweeter\nLighting: Custom RGB 360 LED light display\nBattery: Up to 12 hours playtime per charge\nProtection: IP67 waterproof and dustproof\nConnectivity: Bluetooth 5.3 with PartyBoost pairing",
      imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600&q=80",
      isTrending = true,
      stock = 25,
      discountPercentage = 20,
      modelType = "AUDIO"
    ),
    // 9. DSLR Cameras
    ProductEntity(
      id = 9,
      name = "Sony Alpha a7 IV Mirrorless DSLR",
      brand = "Sony",
      category = "Cameras & Drones",
      price = 2499.0,
      originalPrice = 2699.0,
      rating = 4.9,
      reviewsCount = 145,
      description = "The new benchmark for full-frame hybrid cameras. 33MP Exmor R back-illuminated CMOS sensor, 4K 60p 10-bit 4:2:2 video recording, and Real-Time Eye AF tracking for humans, animals, and birds.",
      specs = "Sensor: 33.0 MP Full-Frame Exmor R CMOS\nProcessor: BIONZ XR image processing engine\nAutofocus: 759 phase-detection AF points (94% coverage)\nVideo: 4K 60p 10-bit 4:2:2 internal recording\nStabilization: 5-axis in-body optical image stabilization",
      imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80",
      isBestSeller = true,
      stock = 8,
      discountPercentage = 7,
      modelType = "CAMERA"
    ),
    // 10. Drone Cameras
    ProductEntity(
      id = 10,
      name = "DJI Mavic 3 Pro Tri-Camera Drone",
      brand = "DJI",
      category = "Cameras & Drones",
      price = 2199.0,
      originalPrice = 2399.0,
      rating = 4.9,
      reviewsCount = 188,
      description = "Inspiration in every frame. Equipped with a triple-lens system featuring a 4/3 CMOS Hasselblad camera and dual tele-cameras. 43-minute max flight time and omnidirectional obstacle sensing.",
      specs = "Primary Camera: 4/3 CMOS Hasselblad 20MP 5.1K video\nTele Cameras: 70mm Medium Tele + 166mm Super Telephoto\nFlight Time: Up to 43 minutes max flight time\nTransmission: DJI O3+ 15km 1080p/60fps video feed\nSafety: Omnidirectional APAS 5.0 obstacle sensing",
      imageUrl = "https://images.unsplash.com/photo-1527977966376-1c8408f9f108?w=600&q=80",
      isTrending = true,
      isFlashSale = true,
      stock = 10,
      discountPercentage = 8,
      modelType = "DRONE"
    ),
    // 11. Smart Watches
    ProductEntity(
      id = 11,
      name = "Apple Watch Ultra 2 Titanium GPS+Cellular",
      brand = "Apple",
      category = "Wearables",
      price = 799.0,
      originalPrice = 849.0,
      rating = 4.8,
      reviewsCount = 412,
      description = "The ultimate sports and adventure timepiece. Rugged 49mm titanium case, 3000-nit Always-On Retina display, dual-frequency precision GPS, and up to 72 hours of battery life in low power mode.",
      specs = "Case: 49mm corrosion-resistant aerospace titanium\nDisplay: 3000 nits sapphire crystal flat glass\nWater Resistance: 100m water resistant, EN13319 scuba dive certified\nSensors: Depth gauge, water temp, ECG, blood oxygen\nBattery: 36 hours normal use, 72 hours low power",
      imageUrl = "https://images.unsplash.com/photo-1544117519-31a4b719223d?w=600&q=80",
      isBestSeller = true,
      stock = 20,
      discountPercentage = 6,
      modelType = "WATCH"
    ),
    // 12. Laptops
    ProductEntity(
      id = 12,
      name = "MacBook Pro 16 M4 Max Space Black",
      brand = "Apple",
      category = "Laptops & PC",
      price = 3499.0,
      originalPrice = 3699.0,
      rating = 5.0,
      reviewsCount = 175,
      description = "Mind-bending performance for professional creators. Powered by the M4 Max chip with 16-core CPU and 40-core GPU, 64GB unified memory, and Liquid Retina XDR display with 1600 nits peak brightness.",
      specs = "Processor: Apple M4 Max (16-Core CPU, 40-Core GPU)\nMemory: 64GB Unified Memory | 2TB NVMe SSD\nDisplay: 16.2 inch Liquid Retina XDR ProMotion 120Hz\nPorts: 3x Thunderbolt 5, HDMI 2.1, SDXC card slot, MagSafe 3\nBattery: Up to 22 hours video playback",
      imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&q=80",
      isTrending = true,
      isBestSeller = true,
      isDealOfDay = true,
      stock = 14,
      discountPercentage = 5,
      modelType = "LAPTOP"
    ),
    // 13. Gaming Mouse
    ProductEntity(
      id = 13,
      name = "Razer Viper V3 Pro Wireless Esports Mouse",
      brand = "Razer",
      category = "Gaming & Toys",
      price = 159.99,
      originalPrice = 179.99,
      rating = 4.9,
      reviewsCount = 380,
      description = "Ultra-lightweight 54g design engineered with world-class esports professionals. Features the Focus Pro 35K Gen-2 optical sensor and 8000Hz HyperPolling wireless dongle included.",
      specs = "Weight: Ultra-lightweight 54 grams\nSensor: Focus Pro 35K Gen-2 Optical Sensor\nPolling Rate: Up to 8000 Hz true wireless polling\nSwitches: Optical Mouse Switches Gen-3 (90M clicks)\nBattery: Up to 95 hours constant gameplay",
      imageUrl = "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&q=80",
      isTrending = true,
      stock = 50,
      discountPercentage = 11,
      modelType = "ACCESSORY"
    ),
    // 14. Gaming Keyboard
    ProductEntity(
      id = 14,
      name = "Logitech G PRO X TKL Rapid Trigger Keyboard",
      brand = "Logitech G",
      category = "Gaming & Toys",
      price = 189.99,
      originalPrice = 219.99,
      rating = 4.8,
      reviewsCount = 265,
      description = "Competitive esports keyboard with magnetic analog switches enabling adjustable actuation from 0.1mm to 4.0mm and Rapid Trigger functionality for instant key repeat responses.",
      specs = "Switches: Magnetic Analog switches with Rapid Trigger\nForm Factor: Tenkeyless (TKL) compact tournament layout\nConnectivity: LIGHTSPEED wireless, Bluetooth, and USB-C\nKeycaps: Double-shot PBT textured keycaps\nLighting: LIGHTSYNC RGB per-key customization",
      imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&q=80",
      isBestSeller = true,
      stock = 35,
      discountPercentage = 13,
      modelType = "ACCESSORY"
    ),
    // 15. Headphones
    ProductEntity(
      id = 15,
      name = "Sony WH-1000XM5 Wireless Noise Canceling",
      brand = "Sony",
      category = "Audio & Sound",
      price = 398.0,
      originalPrice = 449.0,
      rating = 4.9,
      reviewsCount = 780,
      description = "Industry-leading noise cancellation powered by two processors and 8 microphones. Specially designed 30mm driver unit provides superior sound quality and crystal-clear hands-free calling.",
      specs = "Noise Cancellation: Dual processors with Auto NC Optimizer\nDriver: Precision 30mm carbon fiber composite driver\nBattery: 30 hours battery life with quick charge (3 min = 3 hrs)\nMicrophones: 8 microphones with beamforming noise reduction\nComfort: Ultra-soft protein leather fit with stepless slider",
      imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&q=80",
      isBestSeller = true,
      isFlashSale = true,
      stock = 45,
      discountPercentage = 11,
      modelType = "AUDIO"
    ),
    // 16. Remote Control Cars
    ProductEntity(
      id = 16,
      name = "Cyber Drift 4WD High Speed Brushless RC Car",
      brand = "HyperDrift",
      category = "Gaming & Toys",
      price = 149.99,
      originalPrice = 199.99,
      rating = 4.7,
      reviewsCount = 140,
      description = "Reach speeds up to 60 KM/H with all-metal differential gears, 3650 brushless waterproof motor, oil-filled alloy shock absorbers, and drifting LED headlights for nighttime racing.",
      specs = "Top Speed: 60+ KM/H (37+ MPH)\nMotor: 3650 KV3300 Brushless Waterproof Motor\nScale: 1:10 professional hobby grade chassis\nBattery: Dual 7.4V 5200mAh Li-Po batteries included\nController: 2.4GHz proportional steering transmitter (150m range)",
      imageUrl = "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=600&q=80",
      isNewArrival = true,
      stock = 20,
      discountPercentage = 25,
      modelType = "ACCESSORY"
    ),
    // 17. Kids Toys
    ProductEntity(
      id = 17,
      name = "RoboMaster S1 STEM Programmable Educational Robot",
      brand = "DJI Edu",
      category = "Gaming & Toys",
      price = 499.0,
      originalPrice = 549.0,
      rating = 4.8,
      reviewsCount = 112,
      description = "Unlock the world of robotics and AI programming. Features 4 Mecanum wheels for omnidirectional movement, 2-axis mechanical gimbal, infrared/gel bead blaster, and Scratch/Python coding support.",
      specs = "Movement: 4x Mecanum wheels with 12 rollers each\nWeapons: Gel bead blaster and infrared beam transmitter\nVision: FPV 1080p camera with AI line/person tracking\nProgramming: Supports Scratch 3.0 and Python scripting\nSensors: 31 intelligent sensors throughout the armor",
      imageUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=600&q=80",
      isTrending = true,
      stock = 15,
      discountPercentage = 9,
      modelType = "ACCESSORY"
    ),
    // 18. Smart TV
    ProductEntity(
      id = 18,
      name = "Samsung 65 inch Neo QLED 8K Smart TV",
      brand = "Samsung",
      category = "Displays & TV",
      price = 2499.0,
      originalPrice = 2999.0,
      rating = 4.8,
      reviewsCount = 190,
      description = "Ultra-precise Quantum Mini LEDs powered by the NQ8 AI Gen3 Processor. Experience jaw-dropping 8K AI Upscaling, Dolby Atmos Object Tracking Sound, and Motion Xcelerator 240Hz for PC gaming.",
      specs = "Resolution: True 8K (7680 x 4320) with Quantum Matrix Pro\nRefresh Rate: Up to 240Hz VRR for next-gen gaming\nAudio: 90W 6.2.4 channel Dolby Atmos speaker array\nDesign: Infinity One design with Ultra Slim 15mm profile\nSmart OS: Tizen OS with Gaming Hub and cloud gaming",
      imageUrl = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600&q=80",
      isBestSeller = true,
      isDealOfDay = true,
      stock = 10,
      discountPercentage = 16,
      modelType = "TV"
    ),
    // 19. Power Bank
    ProductEntity(
      id = 19,
      name = "Anker Prime 27650mAh 250W Smart Power Bank",
      brand = "Anker",
      category = "Accessories",
      price = 179.99,
      originalPrice = 199.99,
      rating = 4.9,
      reviewsCount = 520,
      description = "Colossal capacity with 250W ultra-fast multi-device charging. Power two 16-inch MacBook Pros simultaneously with dual USB-C ports. Smart OLED digital display shows real-time wattage and battery health.",
      specs = "Capacity: 27,650 mAh (99.54Wh airline safe limit)\nTotal Output: 250W Max (140W USB-C1 + 100W USB-C2 + 10W USB-A)\nDisplay: Smart TFT color screen with Bluetooth app sync\nRecharge: 100W rapid recharge in only 37 minutes\nSafety: ActiveShield 2.0 temperature monitoring 3,000,000x per day",
      imageUrl = "https://images.unsplash.com/photo-1609592424209-27d4726e1a12?w=600&q=80",
      isTrending = true,
      stock = 60,
      discountPercentage = 10,
      modelType = "ACCESSORY"
    ),
    // 20. Chargers
    ProductEntity(
      id = 20,
      name = "GaNPrime 140W 4-Port Fast Compact Charger",
      brand = "Anker",
      category = "Accessories",
      price = 89.99,
      originalPrice = 109.99,
      rating = 4.8,
      reviewsCount = 430,
      description = "Replace four bulky chargers with one sleek GaN charger. Features 3x USB-C ports and 1x USB-A port with PowerIQ 4.0 dynamic power distribution to intelligently balance output across devices.",
      specs = "Technology: Gallium Nitride (GaN III) high efficiency architecture\nPorts: 3x USB-C (140W max single port) + 1x USB-A\nCompatibility: MacBook Pro/Air, iPhone 16/15, Galaxy S24, iPad\nSize: 43% smaller than standard Apple 140W charger\nPlug: Foldable US prongs for travel convenience",
      imageUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=600&q=80",
      isBestSeller = true,
      stock = 75,
      discountPercentage = 18,
      modelType = "ACCESSORY"
    ),
    // 21. USB Cables
    ProductEntity(
      id = 21,
      name = "TitanWeave USB-C 240W Braided Cable (6ft)",
      brand = "Belkin Pro",
      category = "Accessories",
      price = 29.99,
      originalPrice = 39.99,
      rating = 4.7,
      reviewsCount = 610,
      description = "Built to last a lifetime with bulletproof Kevlar fiber weave and zinc alloy connector housings. Supports 240W Extended Power Range (EPR) fast charging and 40Gbps Thunderbolt 4 data transfer.",
      specs = "Power Rating: 240W EPR (48V / 5A) USB-IF certified\nData Transfer: Up to 40 Gbps Thunderbolt 4 / USB 4 speed\nVideo Output: Supports 8K 60Hz or dual 4K monitors\nDurability: Tested to withstand 50,000+ bends and 100 kg tension\nLength: 6 Feet (1.8 Meters) tangle-free braided cord",
      imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&q=80",
      isBestSeller = true,
      stock = 100,
      discountPercentage = 25,
      modelType = "ACCESSORY"
    ),
    // 22. Memory Cards
    ProductEntity(
      id = 22,
      name = "SanDisk 1TB Extreme PRO microSDXC UHS-I Card",
      brand = "SanDisk",
      category = "Accessories",
      price = 99.99,
      originalPrice = 149.99,
      rating = 4.9,
      reviewsCount = 820,
      description = "Engineered for 4K and 5K UHD drone and action camera recording. Blazing fast read speeds up to 200MB/s and write speeds up to 140MB/s with A2 app performance rating for seamless smartphone storage expansion.",
      specs = "Capacity: 1 Terabyte (1000 GB)\nSpeeds: Up to 200MB/s Read | Up to 140MB/s Write\nRatings: Class 10, UHS Speed Class 3 (U3), Video Speed Class 30 (V30)\nApp Speed: A2 rating for fast application loading\nDurability: Temperature-proof, water-proof, shock-proof, X-ray proof",
      imageUrl = "https://images.unsplash.com/photo-1562976540-1502c2145186?w=600&q=80",
      isFlashSale = true,
      stock = 90,
      discountPercentage = 33,
      modelType = "ACCESSORY"
    ),
    // 23. Tripods
    ProductEntity(
      id = 23,
      name = "Peak Design Travel Carbon Fiber Tripod",
      brand = "Peak Design",
      category = "Cameras & Drones",
      price = 599.95,
      originalPrice = 649.95,
      rating = 4.9,
      reviewsCount = 215,
      description = "Redesigning the tripod from the ground up. Packs down to the diameter of a water bottle without compromising height or stability. Ultra-light carbon fiber construction with integrated ball head.",
      specs = "Material: Ultra-light weight Carbon Fiber legs\nWeight Capacity: Holds up to 20 lbs (9.1 kg) professional camera rigs\nPacked Diameter: 3.12 inches (packs dead-space free)\nMax Height: 60 inches (152 cm) center column extended\nMount: Integrated ergonomic ball head with Arca-Swiss plate",
      imageUrl = "https://images.unsplash.com/photo-1512790182412-b19e6d62bc39?w=600&q=80",
      isTrending = true,
      stock = 18,
      discountPercentage = 7,
      modelType = "ACCESSORY"
    ),
    // 24. Monitors
    ProductEntity(
      id = 24,
      name = "Alienware 32 inch 4K QD-OLED 240Hz Curved Monitor",
      brand = "Alienware",
      category = "Displays & TV",
      price = 1199.99,
      originalPrice = 1399.99,
      rating = 4.9,
      reviewsCount = 290,
      description = "The world's first 4K QD-OLED gaming monitor. Immerse yourself in infinite contrast, 0.03ms GtG response time, true Dolby Vision HDR, and 1700R subtle curve with customizable AlienFX RGB lighting.",
      specs = "Panel: 31.6 inch True 4K QD-OLED (3840 x 2160)\nRefresh Rate: 240Hz native | Response Time: 0.03ms GtG\nColor: 99% DCI-P3 color gamut, Delta E < 2 color accuracy\nConnectivity: 2x HDMI 2.1, 1x DisplayPort 1.4, USB 3.2 Gen1 hub\nWarranty: 3-year OLED burn-in hardware warranty",
      imageUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600&q=80",
      isBestSeller = true,
      isDealOfDay = true,
      stock = 16,
      discountPercentage = 14,
      modelType = "TV"
    ),
    // 25. Gaming Chair
    ProductEntity(
      id = 25,
      name = "Secretlab TITAN Evo Cyberpunk 2077 Edition",
      brand = "Secretlab",
      category = "Gaming & Toys",
      price = 619.0,
      originalPrice = 669.0,
      rating = 4.9,
      reviewsCount = 450,
      description = "Engineered for uncompromising ergonomic comfort during all-night gaming sessions. Features 4-way L-ADAPT lumbar support system, magnetic memory foam head pillow, and NEO Hybrid Leatherette.",
      specs = "Upholstery: Secretlab NEO Hybrid Leatherette (12x more durable)\nLumbar Support: Integrated 4-way L-ADAPT mechanical lumbar\nArmrests: Full metal 4D armrests with CloudSwap replacement tops\nRecline: 165 degree multi-tilt mechanism with rock function\nRecommended Size: Regular (170-189 cm / <100 kg weight limit)",
      imageUrl = "https://images.unsplash.com/photo-1598550476439-6847785fdd6a?w=600&q=80",
      isTrending = true,
      isBestSeller = true,
      stock = 20,
      discountPercentage = 7,
      modelType = "CHAIR"
    )
  )

  val coupons = listOf(
    CouponEntity(1, "TREND20", 20, 50.0, "Get 20% OFF on your entire cart (Min order $50)"),
    CouponEntity(2, "WELCOME50", 15, 100.0, "Special $50 equivalent value discount for new customers!"),
    CouponEntity(3, "CYBER30", 30, 500.0, "Massive 30% discount on high-end gaming and laptop rigs!"),
    CouponEntity(4, "FREEFLY", 10, 30.0, "Free express drone delivery + 10% discount on accessories")
  )

  val banners = listOf(
    AdBannerEntity(
      id = 1,
      title = "CYBERWEEK MEGA SALE",
      subtitle = "Up to 35% OFF on Flagship 3D Tech & AI Smartphones",
      imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80",
      targetCategory = "Smartphones",
      adType = "TOP_BANNER",
      discountBadge = "35% OFF"
    ),
    AdBannerEntity(
      id = 2,
      title = "NEXT-GEN GAMING RIGS",
      subtitle = "RTX 5090 Laptops & 240Hz QD-OLED Monitors",
      imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80",
      targetCategory = "Gaming & Toys",
      adType = "CAROUSEL",
      discountBadge = "NEW ARRIVAL"
    ),
    AdBannerEntity(
      id = 3,
      title = "STUDIO GRADE AUDIO",
      subtitle = "AirPods Pro 2 & Sony WH-1000XM5 Noise Canceling",
      imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
      targetCategory = "Audio & Sound",
      adType = "SIDEBAR",
      discountBadge = "BEST SELLER"
    ),
    AdBannerEntity(
      id = 4,
      title = "LIMITED FLASH OFFER!",
      subtitle = "Use code TREND20 at checkout for instant savings!",
      imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&q=80",
      targetCategory = "Accessories",
      adType = "POPUP",
      discountBadge = "CODE: TREND20"
    )
  )

  val reviews = listOf(
    ReviewEntity(1, 1, "Alex Mercer", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&q=80", 5, "The titanium finish is unreal! The battery easily lasts a full two days of heavy use.", "2 days ago"),
    ReviewEntity(2, 1, "Sarah Jenkins", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&q=80", 5, "Upgraded from iPhone 13 Pro. The camera zoom and 120Hz display are game changers.", "1 week ago"),
    ReviewEntity(3, 2, "David Kim", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&q=80", 5, "Galaxy AI translation and photo editing features work like magic. Best Android phone ever made.", "3 days ago"),
    ReviewEntity(4, 12, "Elena Rostova", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&q=80", 5, "Rendering 3D animations in Three.js and Blender takes half the time compared to my old PC!", "4 days ago"),
    ReviewEntity(5, 7, "Marcus Vance", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&q=80", 5, "Active noise cancellation blocks out jet engine noise completely on my flights.", "Yesterday")
  )
}
