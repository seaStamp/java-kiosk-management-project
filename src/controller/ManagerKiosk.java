package controller;

import input.InputDevice;
import model.Menu;
import model.Product;
import model.Store;
import view.ManagerScreen;

import java.util.HashMap;
import java.util.List;

public class ManagerKiosk extends Kiosk {
    // status 상수값
    public static ManagerKisokStatus status;
    private static final ManagerScreen screen = new ManagerScreen();
    private static final Store store = new Store();

    public static void managerKioskStart() {
        status = ManagerKisokStatus.MAIN_MENU;
        while (true) {
            switch (status) {
                case HOME -> {
                    return;
                }
                case MAIN_MENU -> {
                    screen.displayMainMenu();
                    handleMainMenu();
                }
                case WAITNG_ORDER_LIST -> {
                    screen.waitingOrderList(Store.waitingList);
                    handleWaitingOrderListMenu();
                }
                case COMPLETED_ORDER_LIST -> {
                    screen.orderCompleteList(Store.completedList);
                    handleCompletedOrderListMenu();
                }
                case CREAT_PRODUCT -> {
                    screen.productCreateMenu(Store.menuList);
                    handleCreateProduct(Store.menuList);
                }
                case DELETE_PRODUCT -> {
                    screen.productDeleteMenu(Store.menuList);
                    handleDeleteProduct(Store.menuList, Store.menus);
                }
            }
        }
    }

    private static void handleMainMenu() {
        status = ManagerKisokStatus.MAIN_MENU;
        switch (InputDevice.receiveInt(0, 4)) {
            case 0 -> {
                status = ManagerKisokStatus.HOME;
            }
            case 1 -> {
                status = ManagerKisokStatus.WAITNG_ORDER_LIST;
            }
            case 2 -> {
                status = ManagerKisokStatus.COMPLETED_ORDER_LIST;
            }
            case 3 -> {
                status = ManagerKisokStatus.CREAT_PRODUCT;
            }
            case 4 -> {
                status = ManagerKisokStatus.DELETE_PRODUCT;
            }
        }
    }

    private static void handleWaitingOrderListMenu() {

        int selectedNumber = InputDevice.receiveInt(0, Store.waitingList.size());
        int answer;
        if (selectedNumber == 0) {
            status = ManagerKisokStatus.MAIN_MENU;
        } else if (selectedNumber != -1) {
            do {
                screen.orderComplete(Store.waitingList.get(selectedNumber - 1));
                answer = InputDevice.receiveInt(1, 2);
            } while (answer == -1);
            status = ManagerKisokStatus.MAIN_MENU;
            if (answer == 1) {
                store.changeCompleteOrderState(Store.waitingList.get(selectedNumber - 1));
            }
        }
    }


    // 완료주문 목록
    private static void handleCompletedOrderListMenu() { // 매개변수로 받을 것 : List<Order>
        int selectedNumber = InputDevice.receiveInt(0, Store.completedList.size());
        if (selectedNumber == 0) {
            status = ManagerKisokStatus.MAIN_MENU;
        } else if (selectedNumber != -1) {
            status = ManagerKisokStatus.MAIN_MENU;
            screen.orderCompleteNumber(Store.completedList.get(selectedNumber - 1));
        }
    }

    // 상품생성 핸들러
    private static void handleCreateProduct(List<Menu> menuList) { // 리팩터링해야함
        int selectedNumber = InputDevice.receiveInt(1, menuList.size() + 1);
        int answer;
        String menuName = null;
        String menuInfo = null;
        String productName;
        String productInfo;
        double productPrice;

        if (selectedNumber != -1) {
            {
                if (selectedNumber == Store.menuList.size() + 1) { // 신규메뉴 선택
                    System.out.print("생성할 메뉴 이름을 입력해주세요 : ");
                    menuName = InputDevice.receiveString();
                    System.out.println("생성할 메뉴에 대한 설명을 입력해주세요 : ");
                    menuInfo = InputDevice.receiveString();
                } else {
                    menuName = Store.menuList.get(selectedNumber - 1).getName();
                    menuInfo = Store.menuList.get(selectedNumber - 1).getInfo();
                }

                System.out.print("생성할 상품의 이름을 입력해주세요 : ");
                productName = InputDevice.receiveString();
                System.out.print("생성할 상품에 대한 설명을 입력해주세요 : ");
                productInfo = InputDevice.receiveString();
                System.out.print("생성할 상품의 가격을 입력해주세요 :  ");
                do {
                    productPrice = InputDevice.receiveDouble();  // 추가로 예외처리 해야할 수도있음
                } while (productPrice == -1);
                screen.productCreate(menuName, menuInfo, productName, productInfo, productPrice);
                status = ManagerKisokStatus.MAIN_MENU;
                do {
                    answer = InputDevice.receiveInt(1, 2);
                } while (answer == -1);
                if (answer == 1) {
                    if (selectedNumber == Store.menuList.size() + 1) {
                        store.createMenu(menuName, menuInfo);
                    }
                    store.createProduct(menuList.get(selectedNumber - 1).getName(), productName, productInfo, productPrice);
                }
            }
        }
    }


    private static void handleDeleteProduct(List<Menu> menuList, HashMap<String, List<Product>> menus) {
        int selectedNumber = InputDevice.receiveInt(1, menus.size());
        int answer = -1;
        Menu menu;
        List<Product> products;
        if (selectedNumber != -1) {// 에러가 나지 않는다면
            menu = menuList.get(selectedNumber - 1);
            products = menus.get(menu.getName());
            screen.productDeleteSelect(products);
            do {
                selectedNumber = InputDevice.receiveInt(1, products.size());
            } while (selectedNumber == -1);
            screen.deleteReconfirm();
            do {
                answer = InputDevice.receiveInt(1, 2);
            } while (answer == -1);
            if (answer == 1) {
                store.deleteProduct(menu, products.get(selectedNumber - 1));
            }
            status = ManagerKisokStatus.MAIN_MENU;
        }
    }

}
