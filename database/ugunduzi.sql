-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Servidor: 192.168.86.55
-- Tiempo de generación: 17-04-2018 a las 12:05:37
-- Versión del servidor: 5.5.57-0+deb7u1-log
-- Versión de PHP: 5.3.29-1~dotdeb.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `ugunduzi`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `crop`
--

CREATE TABLE IF NOT EXISTS `crop` (
  `crop_id` int(10) unsigned NOT NULL,
  `crop_name` varchar(30) NOT NULL,
  `crop_variety` varchar(30) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `crop`
--

INSERT INTO `crop` (`crop_id`, `crop_name`, `crop_variety`) VALUES
(1, 'Mahindi', ''),
(2, 'Mihogo', ''),
(3, 'Choroko', ''),
(4, 'Alizeti', '');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `data_item`
--

CREATE TABLE IF NOT EXISTS `data_item` (
  `data_item_id` int(10) unsigned NOT NULL,
  `data_item_name` varchar(100) NOT NULL,
  `data_item_default_units_id` int(10) unsigned NOT NULL,
  `data_item_type` int(10) unsigned NOT NULL COMMENT '0=number, 1=date, 2=cost',
  `is_crop_specific` tinyint(1) NOT NULL,
  `is_treatment_specific` tinyint(1) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `data_item`
--

INSERT INTO `data_item` (`data_item_id`, `data_item_name`, `data_item_default_units_id`, `data_item_type`, `is_crop_specific`, `is_treatment_specific`) VALUES
(1, 'Land preparation', 0, 1, 0, 0),
(2, 'Planting', 0, 1, 1, 0),
(3, 'Harvesting', 0, 1, 1, 0),
(4, 'Yield', 1, 0, 1, 0),
(5, 'Application (treatment)', 0, 1, 0, 1),
(6, 'Costs (crop)', 4, 2, 1, 0),
(7, 'Costs (treatment)', 4, 2, 0, 1),
(8, 'Sales', 4, 2, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `farm`
--

CREATE TABLE IF NOT EXISTS `farm` (
  `farm_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `farm_name` varchar(30) NOT NULL,
  `farm_size_acres` int(10) unsigned NOT NULL,
  `farm_date_created` date NOT NULL,
  `parent_farm_id` int(10) unsigned NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `farm`
--

INSERT INTO `farm` (`farm_id`, `user_id`, `farm_name`, `farm_size_acres`, `farm_date_created`, `parent_farm_id`) VALUES
(12, 2, 'Shamba 2', 1, '2018-03-29', 0),
(13, 2, 'Shamba 3', 1, '2018-03-29', 0),
(14, 2, 'Shamba 4', 1, '2018-04-02', 0),
(15, 2, 'Shamba 5', 1, '2018-04-04', 0),
(16, 4, 'Shamba 1', 1, '2018-04-09', 0),
(17, 2, 'Shamba 6', 1, '2018-04-09', 0),
(18, 2, 'Shamba 7', 1, '2018-04-09', 0),
(19, 2, 'Shamba 1', 1, '2018-04-13', 0),
(20, 1, 'Shamba 1', 1, '2018-04-13', 0),
(21, 1, 'Shamba 2', 1, '2018-04-16', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `log`
--

CREATE TABLE IF NOT EXISTS `log` (
  `log_id` int(10) unsigned NOT NULL,
  `plot_id` int(10) unsigned NOT NULL,
  `log_date` date NOT NULL,
  `log_data_item_id` int(10) unsigned NOT NULL,
  `log_value` int(10) unsigned NOT NULL,
  `log_units_id` int(10) unsigned NOT NULL,
  `log_crop_id` int(10) unsigned NOT NULL,
  `log_treatment_id` int(10) unsigned NOT NULL,
  `log_picture` varchar(100) NOT NULL,
  `log_sound` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `plot`
--

CREATE TABLE IF NOT EXISTS `plot` (
  `plot_id` int(10) unsigned NOT NULL,
  `internal_plot_id` int(10) unsigned NOT NULL,
  `farm_id` int(10) unsigned NOT NULL,
  `plot_x` int(10) unsigned NOT NULL,
  `plot_y` int(10) unsigned NOT NULL,
  `plot_w` int(10) unsigned NOT NULL,
  `plot_h` int(10) unsigned NOT NULL,
  `plot_crop1` int(10) unsigned NOT NULL,
  `plot_crop2` int(10) unsigned NOT NULL,
  `plot_treatment1` int(10) unsigned NOT NULL,
  `plot_treatment2` int(10) unsigned NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `plot`
--

INSERT INTO `plot` (`plot_id`, `internal_plot_id`, `farm_id`, `plot_x`, `plot_y`, `plot_w`, `plot_h`, `plot_crop1`, `plot_crop2`, `plot_treatment1`, `plot_treatment2`) VALUES
(55, 0, 12, 0, 0, 2, 4, 1, 0, 0, 0),
(56, 1, 12, 2, 0, 2, 4, 1, 3, 0, 0),
(57, 0, 13, 0, 0, 1, 1, 1, 3, 2, 0),
(58, 0, 14, 0, 0, 1, 3, 1, 0, 1, 0),
(59, 1, 14, 1, 0, 1, 4, 2, 0, 2, 0),
(60, 2, 14, 2, 0, 2, 4, 3, 0, 0, 0),
(61, 3, 14, 0, 3, 1, 1, 0, 0, 0, 0),
(62, 0, 15, 1, 1, 2, 2, 1, 0, 0, 0),
(63, 1, 15, 0, 0, 1, 1, 1, 0, 0, 0),
(64, 2, 15, 1, 0, 1, 1, 1, 0, 2, 0),
(65, 3, 15, 2, 0, 1, 1, 1, 0, 1, 0),
(66, 4, 15, 3, 0, 1, 1, 1, 0, 2, 1),
(67, 5, 15, 0, 1, 1, 1, 1, 0, 2, 0),
(68, 6, 15, 3, 1, 1, 1, 1, 0, 1, 0),
(69, 7, 15, 0, 2, 1, 1, 1, 0, 2, 1),
(70, 8, 15, 3, 2, 1, 1, 1, 0, 0, 0),
(71, 9, 15, 0, 3, 1, 1, 1, 0, 1, 0),
(72, 10, 15, 1, 3, 1, 1, 1, 0, 2, 0),
(73, 11, 15, 2, 3, 1, 1, 1, 0, 2, 1),
(74, 12, 15, 3, 3, 1, 1, 1, 0, 1, 0),
(75, 0, 16, 0, 0, 2, 2, 3, 0, 2, 0),
(78, 0, 17, 1, 1, 3, 3, 3, 0, 1, 2),
(79, 0, 18, 0, 0, 3, 2, 1, 0, 0, 0),
(82, 0, 19, 0, 0, 4, 2, 1, 0, 2, 0),
(83, 1, 19, 0, 2, 4, 2, 2, 0, 1, 0),
(84, 0, 20, 0, 0, 4, 2, 1, 0, 2, 0),
(85, 1, 20, 0, 2, 4, 2, 2, 0, 1, 0),
(86, 0, 21, 0, 0, 1, 1, 1, 0, 2, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `treatment`
--

CREATE TABLE IF NOT EXISTS `treatment` (
  `treatment_id` int(10) unsigned NOT NULL,
  `treatment_name` varchar(30) NOT NULL,
  `treatment_category` tinyint(3) unsigned NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `treatment`
--

INSERT INTO `treatment` (`treatment_id`, `treatment_name`, `treatment_category`) VALUES
(1, 'Pest control', 0),
(2, 'Compost', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `units`
--

CREATE TABLE IF NOT EXISTS `units` (
  `units_id` int(10) unsigned NOT NULL,
  `units_name` varchar(100) NOT NULL,
  `units_type` int(10) unsigned NOT NULL COMMENT '0=number, 1=date, 2=cost'
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `units`
--

INSERT INTO `units` (`units_id`, `units_name`, `units_type`) VALUES
(1, 'Kg', 0),
(2, 'Baskets', 0),
(4, 'TZS', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int(10) unsigned NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `user_alias` varchar(30) NOT NULL,
  `user_password` varchar(30) NOT NULL,
  `user_mobile` varchar(30) NOT NULL,
  `user_group` varchar(30) NOT NULL,
  `user_association` varchar(30) NOT NULL,
  `user_location` varchar(30) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`user_id`, `user_name`, `user_alias`, `user_password`, `user_mobile`, `user_group`, `user_association`, `user_location`) VALUES
(1, 'Test user', 'test', 'test', '', '', '', ''),
(2, 'Eugenio Tisselli', 'eugenio', 'cubo23', '', '', '', ''),
(4, '', 'eg', 'gh', '', '', '', '');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `crop`
--
ALTER TABLE `crop`
  ADD PRIMARY KEY (`crop_id`);

--
-- Indices de la tabla `data_item`
--
ALTER TABLE `data_item`
  ADD PRIMARY KEY (`data_item_id`);

--
-- Indices de la tabla `farm`
--
ALTER TABLE `farm`
  ADD PRIMARY KEY (`farm_id`);

--
-- Indices de la tabla `log`
--
ALTER TABLE `log`
  ADD PRIMARY KEY (`log_id`);

--
-- Indices de la tabla `plot`
--
ALTER TABLE `plot`
  ADD PRIMARY KEY (`plot_id`);

--
-- Indices de la tabla `treatment`
--
ALTER TABLE `treatment`
  ADD PRIMARY KEY (`treatment_id`);

--
-- Indices de la tabla `units`
--
ALTER TABLE `units`
  ADD PRIMARY KEY (`units_id`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `crop`
--
ALTER TABLE `crop`
  MODIFY `crop_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT de la tabla `data_item`
--
ALTER TABLE `data_item`
  MODIFY `data_item_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT de la tabla `farm`
--
ALTER TABLE `farm`
  MODIFY `farm_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=22;
--
-- AUTO_INCREMENT de la tabla `log`
--
ALTER TABLE `log`
  MODIFY `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT de la tabla `plot`
--
ALTER TABLE `plot`
  MODIFY `plot_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=87;
--
-- AUTO_INCREMENT de la tabla `treatment`
--
ALTER TABLE `treatment`
  MODIFY `treatment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT de la tabla `units`
--
ALTER TABLE `units`
  MODIFY `units_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
