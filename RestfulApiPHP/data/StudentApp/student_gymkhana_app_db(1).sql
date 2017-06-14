-- phpMyAdmin SQL Dump
-- version 4.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 05, 2017 at 04:53 AM
-- Server version: 10.1.19-MariaDB
-- PHP Version: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `student_gymkhana_app_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `actions`
--

CREATE TABLE `actions` (
  `action_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `action_type` varchar(50) NOT NULL,
  `action_content` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `images`
--

CREATE TABLE `images` (
  `id` int(11) NOT NULL,
  `binay_image` text NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `time_stamp` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `post_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `post_topic` varchar(50) NOT NULL,
  `post_content` text NOT NULL,
  `post_image_id` int(11) DEFAULT NULL,
  `time_stamp` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`post_id`, `student_id`, `post_topic`, `post_content`, `post_image_id`, `time_stamp`) VALUES
(1, 1, 'general', 'This is test content', 5526, 0),
(7, 2, 'general', 'ye suryaveer ne post dala hai', 5526, 0),
(8, 2, 'general', 'timestamp wali post', 5526, 1486245364),
(9, 2, 'general', 'timestamp wali post', 0, 1486264437);

-- --------------------------------------------------------

--
-- Table structure for table `post_bin`
--

CREATE TABLE `post_bin` (
  `id` int(11) NOT NULL,
  `post_topic` varchar(100) NOT NULL,
  `post_content` text NOT NULL,
  `original_post_id` int(11) NOT NULL,
  `update_time_stamp` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `student_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `profile_pic` text,
  `authentication_token` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `name`, `email`, `password`, `profile_pic`, `authentication_token`) VALUES
(1, 'Ankit Gaur', 'ee150002007@iiti.ac.in', 'b064a40df0455d40026e5e0ba7c5cb6b', NULL, '5eb62a7de1043149aff35b8625aaf815'),
(2, 'Suryaveer', 'ee150002037@iiti.ac.in', 'b064a40df0455d40026e5e0ba7c5cb6b', NULL, '7c888d5b70e60cc80fa2b09b8a231a64');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `actions`
--
ALTER TABLE `actions`
  ADD PRIMARY KEY (`action_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `post_id` (`post_id`);

--
-- Indexes for table `images`
--
ALTER TABLE `images`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`post_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `post_bin`
--
ALTER TABLE `post_bin`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`student_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `actions`
--
ALTER TABLE `actions`
  MODIFY `action_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `images`
--
ALTER TABLE `images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `post_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `post_bin`
--
ALTER TABLE `post_bin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `actions`
--
ALTER TABLE `actions`
  ADD CONSTRAINT `actions_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `actions_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`);

--
-- Constraints for table `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
