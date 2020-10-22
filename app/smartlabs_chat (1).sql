-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-10-2020 a las 09:10:32
-- Versión del servidor: 10.4.14-MariaDB
-- Versión de PHP: 7.4.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `smartlabs_chat`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chats`
--

CREATE TABLE `chats` (
  `chat_id` varchar(200) NOT NULL,
  `inicio` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `chats`
--

INSERT INTO `chats` (`chat_id`, `inicio`) VALUES
('1602738568655', '2020-10-15 07:09:28');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mensajes`
--

CREATE TABLE `mensajes` (
  `mensaje_id` varchar(200) NOT NULL,
  `telefono` varchar(11) DEFAULT NULL,
  `dia` datetime DEFAULT NULL,
  `contenido` varchar(200) DEFAULT NULL,
  `chat_id` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `mensajes`
--

INSERT INTO `mensajes` (`mensaje_id`, `telefono`, `dia`, `contenido`, `chat_id`) VALUES
('1602738577022', '8555444', '2020-10-15 07:09:37', 'hola boob', '1602738568655');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `telefono` varchar(11) NOT NULL,
  `nombre` varchar(20) DEFAULT NULL,
  `token` varchar(1200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`telefono`, `nombre`, `token`) VALUES
('15555215554', 'Pedro', 'eHtBR2p1TliK8dWyazdO73:APA91bFJoi5k0QSAlnbllfsGB4lnEu9NS35kiI9UKSHcfdaDDP0Pd-qdPrSfYno8MEiBeTIbG_j33uscDvuh5HPR_O2chYbmX5QCDGUT0y2h4YLCKxy6D9xHRA2UYA_xF69Dhb_XFc3V'),
('15555215556', 'Bob', 'dbMjzbyeRvK7H7X0JPFRml:APA91bGnxgY1r1waKY2Knmbc5kiSjtK12Z_IJkDmjKsJ7YuDvSN5w6phiWoIhGbTeEMOx89_78FUbluUr9CxMdb-vhnpp61IYwaJipBh4m0O66n0SSDlKl4hQT57uhdllhmL6rJacmFB'),
('2222222', 'nombre2', 'eHtBR2p1TliK8dWyazdO73:APA91bFJoi5k0QSAlnbllfsGB4lnEu9NS35kiI9UKSHcfdaDDP0Pd-qdPrSfYno8MEiBeTIbG_j33uscDvuh5HPR_O2chYbmX5QCDGUT0y2h4YLCKxy6D9xHRA2UYA_xF69Dhb_XFc3V'),
('8555444', 'nombre', 'eHtBR2p1TliK8dWyazdO73:APA91bFJoi5k0QSAlnbllfsGB4lnEu9NS35kiI9UKSHcfdaDDP0Pd-qdPrSfYno8MEiBeTIbG_j33uscDvuh5HPR_O2chYbmX5QCDGUT0y2h4YLCKxy6D9xHRA2UYA_xF69Dhb_XFc3V');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `chats`
--
ALTER TABLE `chats`
  ADD PRIMARY KEY (`chat_id`);

--
-- Indices de la tabla `mensajes`
--
ALTER TABLE `mensajes`
  ADD PRIMARY KEY (`mensaje_id`),
  ADD KEY `chat_mensaje` (`chat_id`),
  ADD KEY `pk_mensaje_usuario` (`telefono`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`telefono`);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `mensajes`
--
ALTER TABLE `mensajes`
  ADD CONSTRAINT `pk_chat_mensaje` FOREIGN KEY (`chat_id`) REFERENCES `chats` (`chat_id`),
  ADD CONSTRAINT `pk_mensaje_usuario` FOREIGN KEY (`telefono`) REFERENCES `usuarios` (`telefono`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
