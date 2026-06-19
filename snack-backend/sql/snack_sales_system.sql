-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: snack_sales_system
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区/县',
  `detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,1,'黄先生','13900139001','广西','南宁市','青秀区','民族大道100号',1,'2026-03-02 22:30:27','2026-03-02 22:30:27'),(2,2,'小王','13900139002','广西','南宁市','西乡塘区','大学东路188号',1,'2026-03-02 22:30:27','2026-03-02 22:30:27');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'Admin','$2a$10$JsAwOljjeDvVoDNSp0OTPeFLWlA0GCgNLlRt033v0NELfzrxO53Q.','19378543467','li@qq.com','管理员','/uploads/2026-03-03/fe0e30faf7334c69ab50facd507859dc.jpg','2026-03-02 23:30:32','2026-03-03 00:12:44');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片URL',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类：新品推荐、促销活动、健康指南、品牌故事',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category` (`category`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='资讯表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (4,'	 新品上架：热销零食合集','本周上新多款零食，欢迎选购！','/uploads/2026-03-04/ae73419cca0845feaf2c3f8f68f7589f.png','新品推荐',13,'2026-03-04 10:30:49','2026-03-04 10:30:49'),(5,'热门零食排行榜：本周人气零食推荐','根据平台销量统计，本周最受欢迎的零食包括坚果礼盒、辣味鸭脖、巧克力夹心饼干以及果干零食。多种口味满足不同消费者需求，深受用户喜爱。','/uploads/2026-03-04/8dd0c3318cb24e43820dc8d22993c923.png','热销零食',4,'2026-03-04 10:56:43','2026-03-04 10:56:43'),(6,'2026年，自有品牌正在进入“硬碰硬” 阶段','第一食品资讯\n过去一年，如果你是一家食品品牌的负责人，你大概率已经感受到一个变化：在商超货架上，在会员店爆款榜单上，在即时零售的首页推荐位上，出现得越来越多的，不再只是制造商品牌，而是零售商自己的品牌。\n山姆的Member’s Mark成为必买清单的核心组成；沃尔玛将沃集鲜作为战略级自有品牌进行系统升级；永辉公开提出未来五年打造500支核心自有品牌商品；盒马在产地直采与数字化体系基础上持续强化自营产品矩阵；朴朴、小象、叮咚买菜等区域零售平台也在加码自有品牌布局。\n这并不是一次短期风口，也不是零售端简单的利润补充动作。\n如果从结构层面观察，2026年很可能会成为中国零售自有品牌进入深水区竞争的关键年份，因为这是零售商开始系统性掌握产品定义权的节点。\n而这件事，对食品品牌商的影响，远比表面看到的货架替换要深得多。\n在传统零售模式中，渠道的核心能力是选品、陈列与分销，产品定义权牢牢掌握在品牌商手中，零售商更像是流量分配者与货架管理者，通过规模议价与终端效率获取利润。\n但在流量见顶、货架同质化加剧、渠道利润率持续被压缩的背景下，零售商逐渐意识到，仅仅作为商品流通节点，很难在竞争中形成长期差异化。\n于是，自有品牌从补充品类，升级为战略级能力。\n这一趋势并非情绪化判断，而是有明确数据支撑的结构变化。\n《2025-2026年中国自有品牌发展研究报告》显示，供应商层面，八成企业自有品牌供货额持续增长，大中型制造商成为开发主力军，自有品牌SKU占供应商总SKU的20%，新品占比达五成，且9%的SKU贡献了80%的供货额。\n在凯度消费者指数发布的相关研究中，头部零售商自有品牌的满意度与推荐意愿（NPS）已经接近甚至部分超过全国性品牌，这意味着消费者对于自有品牌的认知正在完成从低价替代向质价比首选的心智跃迁。\n如果把时间拉长来看，中国自有品牌并不是今天才出现，但真正具备规模爆发条件，是在三个变量同时成熟之后。\n首先是供应链能力的成熟。\n中国食品工业在过去十余年完成了规模化扩张，根据中国食品工业协会公开数据，中国食品工业总产值已连续多年保持万亿元规模，食品制造端的ODM能力、冷链体系与自动化水平显著提升。\n在这种背景下，零售商第一次真正具备整合上游资源的能力，而不仅仅是依赖品牌商供货。\n过去品牌商掌握配方、原料、规格标准与成本结构，而零售商只能通过规模压价；今天零售商可以直接参与原料筛选、规格优化与成本重构，从而将产品从制造商品牌逻辑转向渠道品牌逻辑。\n\n\n','/uploads/2026-03-04/a34dad042cfb4c2a9785c8ac9d08e319.png','食品趋向',10,'2026-03-04 11:02:20','2026-03-04 11:02:20'),(7,'从声量到销量，乐事跑通CNY全打法，为渠道注入动销确定性','春节脚步渐近，食饮行业的CNY大战已从“备货期”正式转入刺刀见红的“爆发期”。\n一个月前，《新经销》曾分析过乐事2026年的“场景化货盘”进化。彼时，乐事凭借“福乐桶”、“全家桶”等兼具年味与分量的组合拳，以及覆盖“自用+送礼”全场景的精细化矩阵，率先解决了渠道关于“卖什么”的备货焦虑。但随着春运开启，摆在行业面前的新命题变成了：货铺下去了，动销靠什么点燃？\n近期走访市场时，一位资深经销商的焦虑颇具代表性：“流量被切得太碎，不仅是线上，线下各种新业态也在分流。我们最担心的，是这股春节热闹气，能不能聚拢到我们的货架前？”\n\n带着这份行业体感审视乐事2026 CNY的冲刺动作，我们发现其策略重心已从“货盘占位”升级为“流量承接”。乐事并未止步于存量博弈，而是通过全域声量与场景深耕，全方位强化品牌心智，并为合作伙伴注入强心剂。\n\n这是一套为春节高峰期量身定制的“系统化动销设计”。它向渠道证明：这一年的春节生意，不仅有“好货”，更有“确定性”的动销。\n','/uploads/2026-03-04/7fabcf053fea4e7cade194d6b8ad0997.png','春节营销',12,'2026-03-04 11:04:59','2026-03-04 11:04:59'),(8,'2026年货消费观察：当“00后”接管年货，中国人的年味有了新故事','商务部大数据监测显示，截至2月8日，全国网上年货节网络零售额已达9897.3亿元。这个数字本身并不令人意外——毕竟每年春节都是一场消费的狂欢。真正值得关注的，是这近万亿交易背后发生的变化。\n\n艾媒咨询发布的《2026年中国新春年货消费行为调研数据》给出了关键信号：综合电商平台以45.43%的占比成为消费者首选渠道，生鲜电商平台如盒马、小象超市等以34.01%的占比位列第三。但比渠道结构更值得探究的，是另一组数据——“00后”用户整体成交占比超三成，下单金额同比增长47%。\n当“00后”正式接管年货采购权，他们正在重新定义“备年”的方式。一位在杭州工作的95后女孩给出了她的答案：“过去返乡大包小包，现在人先到家，年货随后就到。在淘宝闪购下单年货礼盒，半小时就能送到家，父母觉得很惊喜。”\n我们看到，2026年的年货消费，是一场由代际更迭驱动的消费进化。从“囤货型”到“体验型”，从“人肉背回”到“即时可达”，从“物质满足”到“情绪价值”——中国人过年的方式在变，但那份对团圆的热望、对亲情的珍视，从未改变。\n\n','/uploads/2026-03-04/9e370d3267b74644acf3a049463e1e40.jpeg','年味故事',14,'2026-03-04 11:22:56','2026-03-04 11:22:56'),(9,'现制酸奶大逃杀，20元“漂亮酸奶”崛起','均价20元，排队1小时\n抛弃贵价的中产，转身拥抱“漂亮酸奶”\n \n\n春节期间，内参君走访多地商场发现，一批“漂亮酸奶”悄然走红，不少品牌直接登上当地酸奶品类好评榜TOP1。\n\n\n\n与传统的现制酸奶相比，这批“漂亮酸奶”，爆点在哪？\n\n>>> 空间场景上，大量融入新中式元素，放大东方意境美。\n传统现制酸奶大多主打运动、健康属性，门店风格偏冷峻、现代；而这批漂亮酸奶，则以“温暖”为主，大量使用木质结构、新鲜绿植与中式陈设，营造中式美学的独特质感。\n\n比如楼下酸奶济南万象城店，面积30㎡左右，直接店里面设置了一个巨大的装置还原传统竹编纸灯笼。店员介绍称，该空间设计以《汉宫春晓图》为灵感，灯笼与周边陈列结合，呼应“楼下”之意。\n\n>>> 产品上，用“糖水铺”的模式做酸奶，将中式元素转化为产品符号。\n不止在场景升级，这批“漂亮酸奶”的产品逻辑同样极具竞争力。它们跳出了西式酸奶的固定框架，用中式糖水铺的思路完成产品重构。\n其一，引入在地食材，打造区域风味差异化。比如懒山碗·云南手作酸奶，主打的就是“用云南的花、果、茶、乳重新诠释手作酸奶”，其杭州的“龙井酪雪”、合肥的“白柚皖山”等特定城市系列产品，凭借地域文化迅速圈粉。\n其二，强调现做手作，放大“健康”与“烟火气”。以李若桃手作酸奶铺为例，其主打“现蒸白糯米+酸奶”，每家门店均设置可视化蒸煮区，配合“每一粒米都是现蒸”的广告语，以烟火气与透明感强化“健康”认知。','/uploads/2026-03-05/3a9a1513a42d4f7c8a2b93aefc949bf7.png','食品商业',11,'2026-03-05 14:27:36','2026-03-05 14:27:36');
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '轮播图标题',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '轮播图副标题',
  `image` varchar(500) NOT NULL COMMENT '图片路径',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (2,'坚果每日优选','办公室囤货必备','/uploads/2026-03-04/9dc32650972b405193b91ca03ddcc30d.png',1,1,'2026-03-03 15:07:44','2026-03-03 15:07:44'),(3,'年货零食礼盒','送礼就选零食大礼包','/uploads/2026-03-04/7dbe09a4c47c44c284f789243caae5d3.png',2,1,'2026-03-04 00:26:55','2026-03-04 00:26:55'),(4,'零食狂欢节限时开启','88元任选15件，畅享美味零食盛宴','/uploads/2026-03-05/6814447b10ae44dd922844fc5bf0db8d.png',3,1,'2026-03-05 15:18:39','2026-03-05 15:18:39');
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (9,2,16,1,'2026-03-05 13:42:17','2026-03-05 13:42:17'),(11,2,10,10,'2026-03-12 14:49:12','2026-03-12 14:49:12'),(16,1,14,5,'2026-03-12 16:09:16','2026-03-12 16:09:16'),(17,1,15,3,'2026-04-26 00:45:52','2026-04-26 00:45:52'),(18,2,13,7,'2026-04-26 01:14:29','2026-04-26 01:14:29');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (13,2,10,'2026-03-12 14:49:11'),(22,1,14,'2026-03-12 16:09:15'),(23,1,9,'2026-03-23 14:36:00');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (1,'欢迎使用零食销售管理系统','平台主营各类零食，支持在线下单与后台管理。','2026-03-02 22:30:55','2026-03-02 22:30:55'),(3,'系统维护通知','尊敬的用户，为提升系统稳定性与服务质量，我们将于 2026 年 3 月 10 日 22:00 至次日 06:00 进行系统维护。维护期间，部分功能可能暂时无法使用，敬请谅解。如有紧急事宜，请联系客服热线：400-XXXX-XXXX。','2026-03-05 16:14:38','2026-03-05 16:14:38');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单详情ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='订单详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (18,14,12,'统一冰红茶','/uploads/2026-03-04/6b8d4a06bfa14f93a75727068705029a.png',3.50,4,14.00,'2026-03-12 14:22:26'),(20,16,10,'达利园小蛋糕','/uploads/2026-03-04/1502c380a8564d6991e97d6653061deb.png',6.80,5,34.00,'2026-03-12 14:49:19'),(21,17,7,'三只松鼠每日坚果','/uploads/2026-03-04/9a424a2979fd42e8b41a90b23d5f35e5.png',30.00,4,120.00,'2026-03-12 15:16:30'),(22,17,14,'臻选认养一头牛有机纯牛奶3.8g蛋白200ml*12盒牛奶整箱送礼','/uploads/2026-03-04/22aea581aada4a088026b6b14c2e489c.png',50.00,1,50.00,'2026-03-12 15:16:30'),(23,17,16,'风味鸡肉干','/uploads/2026-03-04/19fb0ec4f38d4c22aaa7ffefa7a6da09.png',19.90,1,19.90,'2026-03-12 15:16:30'),(24,17,15,'天天喜碳烤大鸭腿开袋即食整箱卤味夜宵零食休闲食品小吃特产熟食','/uploads/2026-03-04/26ef9c5e5e374f5e81b25712480b943d.png',19.90,5,99.50,'2026-03-12 15:16:30'),(26,19,15,'天天喜碳烤大鸭腿开袋即食整箱卤味夜宵零食休闲食品小吃特产熟食','/uploads/2026-03-04/26ef9c5e5e374f5e81b25712480b943d.png',19.90,5,99.50,'2026-03-12 15:24:10'),(27,20,16,'风味鸡肉干','/uploads/2026-03-04/19fb0ec4f38d4c22aaa7ffefa7a6da09.png',19.90,6,119.40,'2026-03-12 15:25:39'),(28,21,14,'臻选认养一头牛有机纯牛奶3.8g蛋白200ml*12盒牛奶整箱送礼','/uploads/2026-03-04/22aea581aada4a088026b6b14c2e489c.png',50.00,5,250.00,'2026-03-12 16:09:44'),(29,22,15,'天天喜碳烤大鸭腿开袋即食整箱卤味夜宵零食休闲食品小吃特产熟食','/uploads/2026-03-04/26ef9c5e5e374f5e81b25712480b943d.png',19.90,3,59.70,'2026-04-26 00:46:03'),(30,23,13,'麻辣王子辣条','/uploads/2026-03-04/cdcbb7c7132a43959f0989a803ffe08d.png',13.00,7,91.00,'2026-04-26 01:14:42');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未支付' COMMENT '支付状态：未支付、已支付',
  `order_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未发货' COMMENT '订单状态：未发货、已发货、已完成、已取消',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货地址',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单备注',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_user_deleted` tinyint(1) DEFAULT '0' COMMENT '0-未删除 1-用户已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_order_status` (`order_status`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_pay_status` (`pay_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (14,'ORD202603121422267752',1,14.00,'已支付','已完成','黄先生','13900139001','广西南宁市青秀区民族大道100号','赶快发货','2026-03-12 14:22:28','2026-03-12 14:23:01','2026-03-12 14:23:28','2026-03-12 14:22:26','2026-03-12 14:22:26',0),(16,'ORD202603121449189461',2,34.00,'已支付','已完成','小王','13900139002','广西南宁市西乡塘区大学东路188号',' #用户已删除#','2026-03-12 14:49:26','2026-03-12 15:04:09','2026-03-12 15:04:15','2026-03-12 14:49:19','2026-03-12 14:49:19',0),(17,'ORD202603121516302054',1,289.40,'已支付','已完成','黄先生','13900139001','广西南宁市青秀区民族大道100号','发货快点','2026-03-12 15:17:09','2026-03-12 15:18:00','2026-03-12 15:18:15','2026-03-12 15:16:30','2026-03-12 15:16:30',0),(19,'ORD202603121524101965',1,99.50,'已支付','已完成','黄先生','13900139001','广西南宁市青秀区民族大道100号','','2026-03-12 15:24:11','2026-03-12 15:24:35','2026-03-12 15:24:41','2026-03-12 15:24:10','2026-03-12 15:24:10',0),(20,'ORD202603121525386458',1,119.40,'已支付','已完成','黄先生','13900139001','广西南宁市青秀区民族大道100号','','2026-03-12 15:25:40','2026-03-12 15:25:52','2026-03-12 15:26:09','2026-03-12 15:25:39','2026-03-12 15:25:39',0),(21,'ORD202603121609443878',1,250.00,'已支付','已完成','黄先生','13900139001','广西南宁市青秀区民族大道100号','尽快安排发货','2026-03-12 16:10:00','2026-03-12 16:10:57','2026-03-12 16:11:52','2026-03-12 16:09:44','2026-03-12 16:09:44',0),(22,'ORD202604260046021860',1,59.70,'已支付','已完成','黄先生','13900139001','广西南宁市青秀区民族大道100号','尽快安排发货','2026-04-26 00:46:04','2026-04-26 00:46:33','2026-04-26 00:46:40','2026-04-26 00:46:03','2026-04-26 00:46:03',0),(23,'ORD202604260114426961',2,91.00,'已支付','已完成','小王','13900139002','广西南宁市西乡塘区大学东路188号','尽快发货','2026-04-26 01:14:44','2026-04-26 01:14:59','2026-04-26 01:15:09','2026-04-26 01:14:42','2026-04-26 01:14:42',0);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '商品描述',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品主图URL',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '袋' COMMENT '单位',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `origin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产地/品牌产地',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '上架' COMMENT '状态：上架、下架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category_id` (`category_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_sales` (`sales`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='零食商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (7,1,'三只松鼠每日坚果','混合坚果，营养丰富，独立小包更方便','/uploads/2026-03-04/9a424a2979fd42e8b41a90b23d5f35e5.png',30.00,'盒',83,'安徽',17,'上架','2026-03-04 09:05:09','2026-03-04 09:35:53'),(8,2,'乐事薯片','经典不同口味薯片，香脆可口','/uploads/2026-03-04/bee61c64bf7b474886796e556cd6dc5d.png',8.50,'包',500,'上海',0,'上架','2026-03-04 09:14:57','2026-03-04 09:35:46'),(9,3,'德芙牛奶巧克力','丝滑口感，香甜不腻','/uploads/2026-03-04/1492dd0036b6429bb7d5e6855c4df072.png',12.80,'盒',500,'福建',0,'上架','2026-03-04 09:24:00','2026-03-04 09:24:00'),(10,4,'达利园小蛋糕','松软香甜，早餐/零食皆可','/uploads/2026-03-04/1502c380a8564d6991e97d6653061deb.png',6.80,'斤',995,'福建',5,'上架','2026-03-04 09:26:20','2026-03-12 14:49:55'),(11,5,'周黑鸭鸭脖香辣味','香辣入味，越嚼越香','/uploads/2026-03-05/f203a23adf134081b0c5e3d594adc8e3.jpg',29.90,'袋',289,'湖北',11,'上架','2026-03-04 09:27:52','2026-03-05 14:55:10'),(12,6,'统一冰红茶','清爽解腻，随时补水','/uploads/2026-03-04/6b8d4a06bfa14f93a75727068705029a.png',3.50,'瓶',494,'广东',6,'上架','2026-03-04 09:28:45','2026-03-04 09:28:45'),(13,2,'麻辣王子辣条','鲜香麻辣，非常好吃，吃了还想吃','/uploads/2026-03-04/cdcbb7c7132a43959f0989a803ffe08d.png',13.00,'斤',993,'湖南',7,'上架','2026-03-04 15:26:13','2026-03-04 15:26:13'),(14,6,'臻选认养一头牛有机纯牛奶3.8g蛋白200ml*12盒牛奶整箱送礼','有机好牛奶黑土种出来','/uploads/2026-03-04/22aea581aada4a088026b6b14c2e489c.png',50.00,'箱',91,'上海',9,'上架','2026-03-04 23:41:24','2026-03-04 23:41:24'),(15,5,'天天喜碳烤大鸭腿开袋即食整箱卤味夜宵零食休闲食品小吃特产熟食','贼香一个腿大个鸭腿大快朵颐','/uploads/2026-03-04/26ef9c5e5e374f5e81b25712480b943d.png',19.90,'袋',87,'河南',13,'上架','2026-03-04 23:43:38','2026-03-04 23:43:38'),(16,6,'风味鸡肉干','耐嚼抗饿，多种口味任你选','/uploads/2026-03-04/19fb0ec4f38d4c22aaa7ffefa7a6da09.png',19.90,'罐',191,'山东',9,'上架','2026-03-04 23:45:41','2026-03-04 23:45:41');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='零食分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (1,'坚果炒货','坚果、瓜子、花生等',1,'2026-03-02 22:30:34','2026-03-02 22:30:34'),(2,'膨化食品','薯片、虾条、米饼等',2,'2026-03-02 22:30:34','2026-03-02 22:30:34'),(3,'糖果巧克力','糖果、巧克力类',3,'2026-03-02 22:30:34','2026-03-02 22:30:34'),(4,'饼干糕点','饼干、蛋糕、面包类',4,'2026-03-02 22:30:34','2026-03-02 22:30:34'),(5,'肉类零食','牛肉干、鸭脖等',5,'2026-03-02 22:30:34','2026-03-02 22:30:34'),(6,'饮料冲调','奶茶、咖啡、饮料等',6,'2026-03-02 22:30:34','2026-03-02 22:30:34');
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` int NOT NULL COMMENT '评分：1-5星',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评价内容',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评价图片URL，多张用逗号分隔',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (2,7,7,1,5,'好吃下次继续买','/uploads/2026-03-04/d0b57b457e6d4082b4452582f58ce67a.jpg','2026-03-04 23:54:09'),(4,10,14,1,5,'商家发货非常快，牛奶好喝','','2026-03-06 14:34:55'),(5,11,11,1,5,'非常好吃','','2026-03-06 14:36:31'),(6,12,7,1,5,'真好吃，发货也快','','2026-03-11 20:26:31'),(7,13,7,1,5,'发货好快，零食也好吃','','2026-03-11 20:33:04'),(8,14,12,1,5,'货已收到','','2026-03-12 14:23:50'),(9,15,11,2,5,'非常好吃','','2026-03-12 14:46:28'),(10,16,10,2,5,'非常好吃，发货也快','','2026-03-12 15:04:32'),(11,17,7,1,5,'货已收到','','2026-03-12 15:18:35'),(12,19,15,1,4,'非常好吃','','2026-03-12 15:24:52'),(13,20,16,1,5,'非常好吃，抗饿','','2026-03-12 15:26:38'),(14,21,14,1,5,'非常好喝','','2026-03-12 16:12:15'),(15,22,15,1,5,'非常好吃','','2026-04-26 00:46:53'),(16,23,13,2,5,'非常好吃，送货也快','','2026-04-26 01:15:23');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '状态：正常、禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_phone` (`phone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'user001','$2a$10$Y7Hjgc.NmLAykB10tyZ6Te08lrNBzjxfuf2BsaWN7kVtH00yo.4o2','13900139001','user001@snack.com','黄先生','/uploads/2026-03-03/0ed344ad672b4338ad9635e093b613d6.jpg','正常','2026-03-02 22:28:51','2026-03-02 22:28:51'),(2,'user002','$2a$10$DC.HBRrRLNudqFSAKLiBP.T.LLh/Lopc8XWhwUZ7/Eu3HJ/vgsemy','13900139002','user002@snack.com','小王','/uploads/2026-04-26/ec4e098052384ca083b427d3fe789235.jpg','正常','2026-03-02 22:28:51','2026-03-02 22:28:51');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'snack_sales_system'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-26  1:28:26
