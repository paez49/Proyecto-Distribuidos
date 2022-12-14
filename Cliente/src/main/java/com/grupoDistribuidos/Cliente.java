package com.grupoDistribuidos;

import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import org.zeromq.ZMsg;

public class Cliente {
    public static String obtenerMensaje(String mensaje) {

        String mensajeR = mensaje.replace("[", "");
        mensajeR = mensajeR.replace("]", "");

        String[] separar = mensajeR.split(",");
        // System.out.println("MENSAJE: " + mensajeR);

        String separado = separar[0];
        // Preparar respuesta
        // separado = separado.replace(" ", "");
        return separado;
    }

    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            System.out.println("Ingrese peticion");
            String peticion = "";
            Scanner entradaEscaner = new Scanner(System.in);

            Socket socketCliente = context.createSocket(SocketType.REQ);
            ZHelper.setId(socketCliente);
            socketCliente.connect("tcp://25.63.93.84:5556");

            boolean terminar = false, logueado = false;
            String userPass = "";

            System.out.println("------------------Bienvenido a la tienda online------------------");
            while (!terminar) {

                System.out.println("\n¿Que deseas hacer?\n");
                System.out.println("L: Loguearte en la tienda");
                System.out.println("C: Realizar consulta de los productos");
                System.out.println("K: Realizar una compra de un producto");
                System.out.println("S: Salir\n");
                peticion = entradaEscaner.nextLine();
                switch (peticion) {
                    case "C":
                        if (logueado) {
                            socketCliente.send(peticion);
                            ZMsg reply = ZMsg.recvMsg(socketCliente);
                            String consulta = obtenerMensaje(reply.toString());

                            String[] consultaR = consulta.split("_");
                            for (String consultaR2 : consultaR) {
                                System.out.println(consultaR2);
                            }

                        } else {
                            System.out.println("Debes estar logueado primero\n");
                        }
                        break;
                    case "K":
                        if (logueado) {
                            System.out.println("Ingresa ID del producto a comprar: ");
                            peticion = entradaEscaner.nextLine();
                            peticion = "K-" + peticion;
                            socketCliente.send(peticion);
                            ZMsg reply = ZMsg.recvMsg(socketCliente);
                            System.out.println(obtenerMensaje("\n" + reply.toString()));
                        } else {
                            System.out.println("Debes estar logueado para poder usar los servicios\n");
                        }
                        break;
                    case "L":
                        if (logueado) {
                            System.out.println("Ya estas logueado.");
                        } else {
                            userPass ="";
                            System.out.println("Ingresa usuario: ");
                            userPass = peticion + "-" + userPass;
                            peticion = entradaEscaner.nextLine();
                            String nombre = peticion;
                            userPass = userPass + peticion;
                            System.out.println("Ingresa contrasenia: ");
                            peticion = entradaEscaner.nextLine();

                            userPass = userPass + "-" + peticion;
                            socketCliente.send(userPass);
                            ZMsg reply = ZMsg.recvMsg(socketCliente);
                            // Respuesta servidor
                            //System.out.println("Cliente: " + reply.toString());
                            String resultado = obtenerMensaje(reply.toString());
                            resultado = resultado.replace(" ", "");
                            if (resultado.equals("1")) {
                                logueado = true;
                                System.out.println("Bienvenid@ " + nombre + "!");
                            } else {
                                System.out.println("Usuario no existente");
                            }
                        }

                        break;

                    case "S":
                        terminar = true;
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Escribe una petición válida.\n");
                }/*
                  * peticion = entradaEscaner.nextLine();
                  * socketCliente.send(peticion);
                  * ZMsg reply = ZMsg.recvMsg(socketCliente);
                  * System.out.println("Cliente: "+reply.toString());
                  */

            }

        }
    }
}