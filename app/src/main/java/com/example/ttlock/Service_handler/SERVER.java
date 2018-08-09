package com.example.ttlock.Service_handler;

public interface SERVER {

    String Server = "http://www.lock.vraxn.com";

    String LOGIN = Server + "/api/users/login";
    String PRODUCT_LIST = Server + "/api/products";
    String CATEGORY_LIST = Server + "/api/products/categories";
    String PRODUCT_DETAIL_ = Server +"/api/products/show";
    String LOGOUT = Server + "/api/users/logout";
    String OUR_SERVICE = Server +"/api/products/services";
    String REGISTER = Server +"/api/users/register";
    String GET_ADRESS = Server +"/api/payment/getAddress";
    String SET_UPDATE = Server +"/api/payment/billAddress";
    String ADD_TO_CART = Server +"/api/cart/add";
    String VIEW_CART = Server +"/api/cart";
    String DELETE_CART = Server +"/api/cart/deletecart";
    String CHECKOUT = Server +"/api/payment/checkout";
    String UPDATE_PAYMENT = Server +"/api/payment/updatePayment";
    String Send_support_message = Server +"/api/messages/create";
    String Get_support_messages = Server +"/api/messages";


}