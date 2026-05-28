CREATE DATABASE IF NOT EXISTS `army`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `army`;

CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_unique` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_` (
  `user_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `xu` int NOT NULL DEFAULT 1000,
  `luong` int NOT NULL DEFAULT 1000,
  `cup` int NOT NULL DEFAULT 0,
  `glass` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `user_profile_user_id_fk`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_glass` (
  `user_id` int NOT NULL,
  `glassID` tinyint NOT NULL,
  `ability` longtext DEFAULT NULL,
  `equipID` longtext DEFAULT NULL,
  `data` longtext DEFAULT NULL,
  `point` smallint NOT NULL DEFAULT 0,
  `level` tinyint NOT NULL DEFAULT 1,
  `exp` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`, `glassID`),
  CONSTRAINT `user_glass_user_id_fk`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `forum_threads` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(160) NOT NULL,
  `category` enum('question','discussion') NOT NULL DEFAULT 'question',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `forum_threads_user_id` (`user_id`),
  KEY `forum_threads_updated_at` (`updated_at`),
  CONSTRAINT `forum_threads_user_id_fk`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `forum_posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `thread_id` int NOT NULL,
  `user_id` int NOT NULL,
  `body` text NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `forum_posts_thread_id` (`thread_id`),
  KEY `forum_posts_user_id` (`user_id`),
  CONSTRAINT `forum_posts_thread_id_fk`
    FOREIGN KEY (`thread_id`) REFERENCES `forum_threads` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `forum_posts_user_id_fk`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

