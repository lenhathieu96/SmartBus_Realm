package com.smartbus_realm.Print;

import android.content.Context;

import android.os.Handler;

import android.text.Layout;
import android.util.Log;

import android.zyapi.CommonApi;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.google.gson.Gson;
import com.smartbus_realm.Model.Company;
import com.smartbus_realm.Model.Ticket;
import com.smartbus_realm.Model.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;

public class PrintModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    public static final String DEVK = "DEVK";
    private static final byte[] blodMin = new byte[]{0x1B, 0x21, 0x08};
    private static final byte[] blodMax = new byte[]{0x1B, 0x21, 0x20};
    private static final byte[] noBlod = new byte[]{0x1B, 0x21, 0x01};
    private static final byte[] center = new byte[]{0x1b, 0x61, 0x01};
    private static final byte[] noCenter = new byte[]{0x1b, 0x61, 0x00};
    private static int mComFd = -1;
    static CommonApi mCommonApi;
    private static final Layout.Alignment BOLD_NORMAL = Layout.Alignment.ALIGN_NORMAL;
    private static final Layout.Alignment BOLD_CENTER = Layout.Alignment.ALIGN_CENTER;
    public static boolean isCanprint = false;
    private byte[] textbytes = new byte[0];
    private final int MAX_RECV_BUF_SIZE = 1024;
    private boolean isOpen = false;
    private byte[] recv;
    private Context context;
    public PrintModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }
    @Override
    public String getName() {
        return "PrintModule";
    }

    public static void open() {
        send(new byte[]{0x1B, 0x23, 0x23, 0x35, 0x36, 0x55, 0x50});
    }
    public void initGPIO() {
        mCommonApi = new CommonApi();
        mComFd = mCommonApi.openCom("/dev/ttyMT3", 115200, 8, 'N', 1);
        if (mComFd > 0) {
            isOpen = true;
            Log.d("dev", "Print module start");
        }
    }
    public static void openGPIO() {
        mCommonApi.setGpioDir(58, 0);
        mCommonApi.getGpioIn(58);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCommonApi.setGpioDir(58, 1);
                mCommonApi.setGpioOut(58, 1);
            }
        }, 500);
    }

    public static void send(byte[] data) {
        if (data == null)
            return;
        if (mComFd > 0) {
            mCommonApi.writeCom(mComFd, data, data.length);
        }
    }

    private void readData() {
        new Thread() {
            public void run() {
                while (isOpen) {
                    try{
                          Log.e("DEVK", "Chay 1read success:");
                        int ret = 0;
                        byte[] buf = new byte[MAX_RECV_BUF_SIZE + 1];
                        ret = mCommonApi.readComEx(mComFd, buf, MAX_RECV_BUF_SIZE, 0, 0);
                        if (ret <= 0) {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        } else {
                            // Log.e("", "1read success:");
                        }
                        recv = new byte[ret];
                        System.arraycopy(buf, 0, recv, 0, ret);     // Co ngoai le !!!! src.length=1025 srcPos=0 dst.length=6 dstPos=0 length=31 java.lang.ArrayIndexOutOfBoundsException AndroidRuntime: FATAL EXCEPTION: Thread-1133 at com.smartbus_realm.qs408.PrintModule$22.run(PrintModule.java:1646) at java.lang.System.arraycopy(Native Method)
                        String str = byteToString(buf, buf.length);
                        if (str.contains("14 00 0C 0F")) {
                            isCanprint = false;
                        } else if(isCanprint == false) {
                            isCanprint = true;

                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        }.start();
    }

    @ReactMethod
    private void init() {
        initGPIO();
        openGPIO();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mComFd > 0) {
                    open();
                    isOpen = true;
                    readData();
                    send(new byte[]{0x1F, 0x1B, 0x1F, (byte) 0x80, 0x04, 0x05, 0x06, 0x66});
                } else {
                    isOpen = false;
                }
            }
        }, 500);
    }

    @ReactMethod
    public void printTicketGoods(String company, String part, String address, String phone,
                                 String mst, String kv, String gia, String ngay, String tram_gui,String nv, String bsx, String ticketNumber, final Promise promise) {

        String str = "";
        str += printTextBoldMinCompany(company + "\n");
        str += prinTextVe(address + "\n");
        str += prinTextVe("MST: " + mst + "       " + phone + "\n");
        str += prinTextVe(ticketNumber + "  " + bsx + "\n");
        str += printTextBoldVe(kv + "\n");
        str += printTextCenterVe(part + "\n");
        str += prinTextVe("Tên+SĐT gửi: _________________________________\n \n");
        str += prinTextVe("Trạm gửi:   "+tram_gui+"\n\n");

        str += prinTextVe("Tên+SĐT nhận: ________________________________\n \n");
        str += prinTextVe("Trạm nhận: ___________________________________\n\n");
        //str += prinTextVe(bsx +"\n");
        str += prinTextVe("Phí gửi: "+ gia +" VNĐ \n");
        str += printTVe("In ngày: ", ngay + " " + "\n\n");

        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x0c, 0x1d});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);

    }

    @ReactMethod
    public void printFreeTicket(String company, String address, String phone, String tax_code, String number, String nameStation, String fullname,
                                      String fullnamecustomer, String time, String expiration_date, Promise promise) {
        String str = "";
        str += printTextCenterVe("" + company + "\n");
        str += prinTextVe(address + "\n");
        str += prinTextVe("MST: " + tax_code + " \t" + "Hotline: " + phone + "\n");
        str += printTextBoldVe("VÉ MIỄN PHÍ" + "\n");
        str += printTextCenterVe("(Liên 2: Giao cho khách hàng)" + "\n");
        str += PrintBoldAndCleanBoldVe("Tuyến số: ", number + " ", "   Trạm: ", nameStation + "\n");
        str += prinTextVe("NV: " + fullname + "\n");
        str += prinTextVe("Tên chủ thẻ: " + fullnamecustomer + "\n");
        str += prinTextVe("Hạn sử dụng thẻ: " + expiration_date + "\n");
        str += printTVe("In ngày: ", time + " " + "\n\n");
        try{
            textbytes = str.getBytes("utf-8");
            send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
            send(new byte[]{0x1d, 0x61, 0x00});
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                        if (isCanprint) {
                            send(textbytes);
                            send(new byte[]{0x0c, 0x1d});
                            send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                            send(new byte[]{0x1D, 0x23, 0x45});
                            promise.resolve(true);

                        }else{
                            promise.reject("Out of Paper", "Out of Paper");
                        }
                        handler.removeCallbacks(this::run);
                    }
            };
            handler.postDelayed(runnable, 500);
        }catch(Exception e){
            promise.reject("Print Failed", e);
        }
    }

    @ReactMethod
    public void printTicketMonth(String part2, String company, String address, String phone,
                                 String mst, String kv, String ts, String tram, String bsx,
                                 String tram1,String tram2, String nv, String ngay,String charge_limit, String year_limit,String month_limit, String tenKH, String fontBlodCompany ,final Promise promise) {
        String str = "";
        if (!fontBlodCompany.equals("null")) {
            str += printTextBoldMinCompany(company + "\n");
        }else {
            str += printTextCenterVe(company + "\n");
        }

        str += prinTextVe(address + "\n");
        str += prinTextVe("MST: " + mst + "      " + "Hotline: " + phone + "\n");
        str += printTextBoldVe(kv + "\n");
        str += printTextCenterVe(part2 + "\n");

        str += PrintBoldAndCleanBoldVe("Tuyến số: ", ts + ".", "  Trạm: ", tram + "\n");
        if (fontBlodCompany.equals("null")) {
            str += prinTextVe(nv + "\n");
        }
        str += printTVe("BSX: ", bsx + "\n");

        str += printTVe("Chủ thẻ: ", tenKH + "\n");
        if (!tram1.equals("null")) {
            str += printTVe("Trạm đầu: ", tram1 + "\n");
        }
        if (!tram2.equals("null")) {
            str += printTVe("Trạm cuối: ", tram2 + "\n");
        }
        if (!charge_limit.equals("null")) {
            str += printTVe("Số lần đã quẹt: ", charge_limit + "\n");
        }
        str += printTVe("Thời hạn SD: ", month_limit+"-"+year_limit + "\n");
        str += printTVe("In ngày: ", ngay + " " + "\n\n");
        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x0c, 0x1d});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);
    }

    @ReactMethod
    public void printDeductionTicket(String company, String address, String phone,
                                     String mst, String ms, String kh, String sv, String kv, String ts,
                                     String tram, String nv, String gia, String ngay, String deduction, String discount, String sodu, final Promise promise) {

        String str = "\n";
        str += printTextCenterVe(company + "\n");
        str += prinTextVe(address + "\n");
        str += prinTextVe("Mẫu số: " + ms + " \t" + "MST: " + mst + "\n");
        str += prinTextVe("Ký hiệu: " + kh + " \t" + "Số vé: " + sv + "\n");

        str += printTextBoldVe(kv + "\n");

        str += printTextCenterVe("(Liên 2: Giao cho khách hàng)" + "\n");

        str += PrintBoldAndCleanBoldVe("Tuyến số: ", ts + " ", "   Trạm: ", tram + "\n");
        str += prinTextVe("NV: " + nv + "\n");

        if (!deduction.equals("null")) {
            str += PrintBoldAndCleanBoldVe("Giá vé: ", gia + " Đ/Lượt", "  Chiết khấu: ", discount + " Đ/Lượt" + "\n");
            str += printTVe("Giảm còn: ", deduction + " Đ/Lượt\n");
        } else {
            if (gia.equals("NaN")) {
                str += PrintBoldAndCleanBoldVe("Giá vé: ", "miễn phí " + " Đ/Lượt", "  Chiết khấu: ", "\n");
            } else {
                str += PrintBoldAndCleanBoldVe("Giá vé: ", gia + " Đ/Lượt", "  Chiết khấu: ", "\n");
            }
            str += printTVe("Giảm còn: ", "\n");
        }
        str += printTextCenterVe("(Giá vé đã bao gồm bảo hiểm hành khách)" + " \n");
        str += printTVe("In ngày: ", ngay + " " + "\n");

        if (!sodu.equals("null")) {
            str += PrintBoldAndCleanBoldVe("Số dư thẻ: ", sodu + " Đ", " \t" + "Hotline: " + phone, "\n\n");
        } else {
            str += prinTextVe("Số dư thẻ: " + " \t\t" + "Hotline: " + phone + "\n\n");
        }
        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x0c, 0x1d});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);

    }
    @ReactMethod
    public void printDeductionTicketALL(String company, String vehicle, String ticket, Promise promise) {
        Gson gson  = new Gson();
        Vehicle vehicleData = gson.fromJson(vehicle,Vehicle.class);
        Company companyData = gson.fromJson(company,Company.class);
        Ticket ticketData = gson.fromJson(ticket, Ticket.class);

        String str = "\n";
        str += printTextBoldMinCompany(companyData.getFull_name() + "\n");
        str += prinTextVe(companyData.getAddress() + "\n");
        str += prinTextVe("MST :" + companyData.getTaxCode() + " \t" + "Mẫu số: " + ticketData.getOrder_code() + "\n");
        str += prinTextVe("Số vé: " + ticketData.getAllocation() + " \t" +"Ký hiệu: " + ticketData.getSign() + "\n");

        str += printTextBoldVe("VÉ XE BUÝT LƯỢT" + "\n");

        str += printTextCenterVe("(Liên 2: Giao cho khách hàng)" + "\n");
        str += PrintBoldAndCleanBoldVe("Tuyến số: ", vehicleData.getRoute_number() + " ", "         BSX: ", vehicleData.getLicense_plates() +"\n");
        str += prinTextVe("Trạm lên: " +ticketData.getStart_station() + "\n");
        str += prinTextVe("Trạm xuống: " + ticketData.getArrive_station() + "\n");
        if (ticketData.getDeduction() != null ) {
            str += PrintBoldAndCleanBoldVe("Giá vé: ", ticketData.getPrice(), "  Chiết khấu: ", ticketData.getDiscount() + " Đ/Lượt" + "\n");
            str += printTVe("Giảm còn: ", ticketData.getDeduction() + " Đ/Lượt" + "\n");
        } else {
            if ( ticketData.getPrice().equals("NaN")) {
                str += PrintBoldAndCleanBoldVe("Giá vé: ", "Miễn phí ", "  Chiết khấu: ", "\n");
            } else {
                str += PrintBoldAndCleanBoldVe("Giá vé: ", ticketData.getPrice() + " Đ/Lượt" , "  Chiết khấu: ", "\n");
            }
            str += printTVe("Giảm còn: ", "\n");
        }
        str += printTextCenterVe( "(Giá vé đã bao gồm bảo hiểm hành khách)"+ " \n");
        str += printTVe("In ngày: ", ticketData.getTime() + " " + "\n");

        if (ticketData.getBalance() != null ) {
            str += PrintBoldAndCleanBoldVe("Số dư thẻ: ", ticketData.getBalance() + " Đ", " \t" + "Hotline: " + companyData.getPhone(), "\n\n");
        } else {
            if (ticketData.getThe_tra_truoc() != null) {
                str += prinTextVe("Số dư thẻ: " + " \t\t" + "Hotline: " + companyData.getPhone() + "\n\n");
            } else {
                str += prinTextVe("Hotline: " + companyData.getPhone() + "\n\n");
            }
        }
        try {
            textbytes = str.getBytes("utf-8");
            send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
            send(new byte[]{0x1d, 0x61, 0x00});
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                public void run() {
                    if (isCanprint) {
                        send(textbytes);
                        send(new byte[]{0x0c, 0x1d});
                        send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                        send(new byte[]{0x1D, 0x23, 0x45});
                        promise.resolve(true);
                    } else {
                        promise.reject("Out of Paper", "Out of Paper");
                    }
                    handler.removeCallbacks(this::run);
                }
            };
            handler.postDelayed(runnable, 500);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("Print Failed", e);
        }



    }

    @ReactMethod
    public void printTotal(
            String company, String address, String phone,
            String mst, String kh,
            String nvBv, String nvLx, String sx, String tuyen, String tienmat,
            String napthe, String hanghoa, String qt_hanghoa, String quetthe, String qrcode, String soluong, String thethang, String totals,
            String ticket, String timeDn,
            String hours, String day, String types, String fontBoldCompany, String visibleFree, final Promise promise
    ) throws JSONException {
        String str = "";
        if (!fontBoldCompany.equals("null")) {
            str += printTextBoldMinCompany(company + "\n");
        } else {
            str += printTextCenterTongKet(company + "\n");
        }

        str += prinTextTongKet(address + "\n");
        if (types.equals("1")) {
            str += printTextBoldTongKet("PHIẾU TẠM THỜI" + "\n");
        } else {
            str += printTextBoldTongKet("TỔNG KẾT" + "\n");
        }

        str += prinTextTongKet("Tên nv bán vé: " + nvBv + "\n");
        if (fontBoldCompany.equals("null")) {
            str += prinTextTongKet("Tên nv lái xe: " + nvLx + "\n");
        }

        str += prinTextTongKet("Số xe: " + sx + "       " + "Tuyến số: " + tuyen + "\n");
        str += prinTextTongKet("Mệnh giá" + " | " + "Tiền mặt" + " | " + "Thẻ" +" | "+"Qr Code"+ " |  " + "Vé"+ "\n");
        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");

        JSONArray jsonResponse = new JSONArray(ticket);
        for (int i = 0; i < jsonResponse.length(); i++) {
            if (i == jsonResponse.length() - 1) {
                str += prinTextTongKet(jsonResponse.getJSONObject(i).getString("price").toString() + " VNĐ" + " "
                        + jsonResponse.getJSONObject(i).getString("qty").toString() + "     "
                        + jsonResponse.getJSONObject(i).getString("charge").toString() + "      "
                        + jsonResponse.getJSONObject(i).getString("qr").toString()+ "       "
                        + jsonResponse.getJSONObject(i).getString("serial").toString() + "\n");

            } else {
                str += prinTextTongKet(jsonResponse.getJSONObject(i).getString("price").toString() + " VNĐ" + " "
                        + jsonResponse.getJSONObject(i).getString("qty").toString() + "     "
                        + jsonResponse.getJSONObject(i).getString("charge").toString() + "      "
                        + jsonResponse.getJSONObject(i).getString("qr").toString()+ "       "
                        + jsonResponse.getJSONObject(i).getString("serial").toString() + "\n");
            }
        }
        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
        str += prinTextTongKet("Tiền mặt: " + " \t\t " + tienmat + " VNĐ" + "\n");
        str += prinTextTongKet("Nạp thẻ + Gia hạn: " + " \t " + napthe + " VNĐ" + "\n");
        str += prinTextTongKet("Quẹt thẻ: " + " \t\t " + quetthe + " VNĐ" + "\n");
        str += prinTextTongKet("Qr Code: " + " \t\t " + qrcode + " VNĐ" + "\n");
        if (!visibleFree.equals("null")) {
            str += prinTextTongKet("Số vé miễn phí: " + " \t " + soluong + "\n");
        }

        str += prinTextTongKet("Số vé tháng: " + " \t " + thethang + "\n");
        if(!hanghoa.equals("null")) {
            str += prinTextTongKet("Tiền hàng: " + " \t\t " + hanghoa+ " VNĐ" + "\n");
        }
        if(!qt_hanghoa.equals("null")) {
            str += prinTextTongKet("Quẹt thẻ (hàng hóa): " + " \t " + qt_hanghoa+ " VNĐ" + "\n");
        }
        str += printBoldAllTongKet("Thu: " + " \t", totals + " VNĐ\n");
        str += prinTextTongKet("Đăng nhập lúc: " + timeDn + "\n");
        str += prinTextTongKet("In lúc: " + hours + " " + day + "\n");
        str += prinTextTongKet("");
        try {
            textbytes = str.getBytes("utf-8");
            send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
            send(new byte[]{0x1d, 0x61, 0x00});
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (isCanprint) {
                        send(textbytes);
                        send(new byte[]{0x1d, 0x0c});
                        send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                        send(new byte[]{0x1D, 0x23, 0x45});
                    }
                }
            }, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void printSupervisor(
            String supervisor_name, String company, String address, String phone, String mst, String kh,
            String nvBv, String nvLx, String sx, String tuyen, String ticket, String timeDn,
            String hours, String day, final Promise promise
    ) throws JSONException {

        String str = "";
        str += printTextCenterTongKet(company + "\n");

        str += prinTextTongKet(address + "\n");
        str += printTextBoldTongKet("PHIẾU GIÁM SÁT" + "\n");

        str += prinTextTongKet("Tên giám sát: " + supervisor_name + "\n");
        str += prinTextTongKet("Tên nv bán vé: " + nvBv + "\n");
        str += prinTextTongKet("Tên nv lái xe: " + nvLx + "\n");

        str += prinTextTongKet("Số xe: " + sx + "       " + "Tuyến số: " + tuyen + "\n");
        str += prinTextTongKet("Mệnh giá  | TM | Thẻ | Qr | Trạm " +  "\n");
        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");

        JSONArray jsonResponse = new JSONArray(ticket);
        for (int i = 0; i < jsonResponse.length(); i++) {
            str += prinTextTongKet(jsonResponse.getJSONObject(i).getString("price").toString() + "      "
                    + jsonResponse.getJSONObject(i).getString("cash").toString() + "    "
                    + jsonResponse.getJSONObject(i).getString("card").toString()+ "   "
                    + jsonResponse.getJSONObject(i).getString("qr").toString() + "   "
                    + jsonResponse.getJSONObject(i).getString("station_name").toString() + "\n");
        }

        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");

        str += prinTextTongKet("Đăng nhập lúc: " + timeDn + "\n");
        str += prinTextTongKet("In lúc: " + hours + " " + day + "\n");
        str += prinTextTongKet("");
        try {
            textbytes = str.getBytes("utf-8");

            send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
            send(new byte[]{0x1d, 0x61, 0x00});
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (isCanprint) {
                        send(textbytes);
                        send(new byte[]{0x1d, 0x0c});
                        send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                        send(new byte[]{0x1D, 0x23, 0x45});
                    }
                }
            }, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void printTotalBLTEndDay(
            String title,String company, String address, String driver_name,
            String subdriver_name,  String license_plates,
            String route_number, String total_cash,  String total_charge,  String total_pos, String total_deposit,
            String total_online, String shifts, String print_at, String free, String motnh, String tienmat_hang, String quetthe_hang,
            String visibleFree
    ) throws JSONException {

        String str = "";
        str += printTextBoldMinCompany(company + "\n");
        str += prinTextTongKet(address + "\n");
        str += printTextBoldTongKet(title + "\n\n");
        str += prinTextTongKet(subdriver_name + "\n");
        str += prinTextTongKet(license_plates + "\n");
        str += prinTextTongKet(route_number + "\n");
        // str += prinTextTongKet("Thoi gian" +  "                   Tien mat" + "\n");
        str += prinTextTongKet("Thời gian" +  "                   Tiền mặt" + "\n");
        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
        String space = " ";
        JSONArray jsonResponse = new JSONArray(shifts);
        for (int i = 0; i < jsonResponse.length(); i++) {
            if (i == jsonResponse.length() - 1) {
                if(i>=9) space = "";
                str += prinTextTongKet(space + (i+1) + ". "
                        + jsonResponse.getJSONObject(i).getString("work_time").toString() +"     "
                        + jsonResponse.getJSONObject(i).getString("total_cash").toString() +"\n");

            } else {
                if(i>=9) space = "";
                str += prinTextTongKet( space + (i+1) + ". "
                        + jsonResponse.getJSONObject(i).getString("work_time").toString()+"     "
                        + jsonResponse.getJSONObject(i).getString("total_cash").toString() +"\n");
            }
        }

        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
        str += prinTextTongKet("TỔNG DOANH THU: " + " \t " + total_cash + " VNĐ" + "\n");
        str += prinTextTongKet("Vé lượt: " + " \t\t " + total_pos + " VNĐ" + "\n");
        str += prinTextTongKet("Quẹt thẻ: " + " \t\t " + total_charge + " VNĐ" + "\n");
        str += prinTextTongKet("Nạp thẻ + Gia hạn: " + " \t " + total_deposit + " VNĐ" + "\n");
        str += prinTextTongKet("MOMO: " + " \t\t " + total_online + " VNĐ" + "\n");
        str += prinTextTongKet("Tiền hàng: " + " \t\t " + tienmat_hang + " VNĐ"+ "\n");
        if (!visibleFree.equals("null")) {
            str += prinTextTongKet("Số vé miễn phí: " + " \t " + free + "\n");
        }
        str += prinTextTongKet("Số vé tháng: " + " \t " + motnh + "\n");
        str += prinTextTongKet("In lúc: " + print_at + "\n");
        str += prinTextTongKet("");
        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x1d, 0x0c});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);
    }

    @ReactMethod
    public void printCard(
            String company, String address, String phone,
            String mst, String mt, String tenct, String nv, String price, String sum,
            String time, String date, String expiration_date, final Promise promise) {
//============================================================DEVK=============================================================================
        String str = "";
        str += printTextCenterCard(company + "\n");
        str += prinTextCard(address + "\n");

//        str += prinTextCard("ĐT: " + phone + "\n");
        str += prinTextCard("ĐT: " + phone + "         " + "MST: " + mst + "\n");
        str += printTextBoldCard("HOÁ ĐƠN THANH TOÁN" + "\n");
        str += printeTextCard("" + "(Nạp thẻ trả trước)" + "\n");

        str += prinTextCard("Mã thẻ: " + "\t" + mt + "\n");

        if (!tenct.equals("null")) {
            str += prinTextCard("Tên chủ thẻ: " + tenct + "\n");
        } else {
            str += prinTextCard("Tên chủ thẻ: "  + "\n");
        }
        str += printTCard("Giá tiền: " + "\t", price + " VNĐ" + "\n");

        str += printTCard("NV: " + "\t", nv + "\n");

        str += printTCard("Tổng số dư:  ", sum + " VNĐ" + "\n");
        str += printTCard("Hạn sử dụng:  ", expiration_date + "\n");
        str += prinTextCard("Mua lúc: " + time + " " + "Ngày: " + date + "\n");

        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x1d, 0x0c});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);
    }

    @ReactMethod
    public void printCardMonthQnic(
            String company, String address, String phone,
            String mst, String mt, String tenct, String nv, String price, String giahan,
            String time, String date, String limited, String mauso, String sign, String ticket_number, String tuyenso, String chang, final Promise promise) {
//============================================================DEVK=============================================================================
        String str = "";
        str += printTextCenterCard(company + "\n");
        str += prinTextCard(address + "\n");
        str += prinTextCard("Mẫu số:" + " " + mauso + " "+ "\t" + " MST: " + mst + "\n");
        str += prinTextCard("Ký hiệu:" + " " + sign + " "+"\t" + " Số vé: " + ticket_number + "\n");
        str += printTextBoldCard("VÉ XE BUÝT THÁNG" + "\n");
        str += printeTextCard("" + "(Liên 2 giao cho khách hàng)" + "\n");
        str += printTCard("Tuyến số:" + " " , tuyenso + "\n");
        str += printTCard("Chặng:" + " ", chang + "\n");
        str += prinTextCard("NV: " + "\t"+ nv + "\n");
        str += printTCard("Mã thẻ: " + "\t", mt + "\n");
        if (!tenct.equals("null")) {
            str += printTCard("Tên chủ thẻ: ", tenct + "\n");
        } else {
            str += printTCard("Tên chủ thẻ: ", "\n");
        }
        str += printTCard("Giá tiền: " + "\t", price + " Đ/Tháng" + "\n");
        if (!limited.equals("null")) {
            str += printTCard("Số lượt: " + "\t", limited +"  lượt/tháng"+ "\n");
        }
        str += printeTextCard("(Giá vé đã bao gồm bảo hiểm hành khách)" + " \n");
        str += prinTextCard("In lúc: " + time + " " + "Ngày: " + date + "\n");
        str += PrintBoldAndCleanBoldVe("Thời hạn SD: ", giahan +"    ", "Hotline: " ,phone+ "\n");
        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x1d, 0x0c});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);
    }

    @ReactMethod
    public void printCardMonth(
            String company, String address, String phone,
            String mst, String mt, String tenct, String nv, String price, String amount_deduction, String discount, String giahan,
            String time, String date, String limited, String mauso, String sign, String ticket_number, String tuyenso, String chang, final Promise promise) {
//============================================================DEVK=============================================================================
        String str = "";
        str += printTextBoldMinCompany(company + "\n");
        str += prinTextCard(address + "\n");
        str += prinTextCard("MST:" + " " +mst + " "+ "\t" + " Mẫu số:: " + mauso + "\n");
        str += prinTextCard("Số vé:" + " " +ticket_number  + " "+"\t" + " Ký hiệu: " + sign + "\n");
        str += printTextBoldCard("VÉ XE BUÝT THÁNG" + "\n");
        str += printeTextCard("" + "(Liên 2 giao cho khách hàng)" + "\n");
        str += printTCard("Tuyến số:" + " " , tuyenso + "\n");
        str += printTCard("Chặng:" + " ", chang + "\n");
        if (!nv.equals("null")) {
            str += prinTextCard("NV: " + "\t"+ nv + "\n");
        }
        str += printTCard("Mã thẻ: " + "\t", mt + "\n");
        if (!tenct.equals("null")) {
            str += printTCard("Tên chủ thẻ: ", tenct + "\n");
        } else {
            str += printTCard("Tên chủ thẻ: ", "\n");
        }
        if (!amount_deduction.equals("null")) {
            str += printTVe("Giá tiền: ", price + "\n");
            str += printTVe("Chiết khấu: ", discount + " Đ/Tháng\n");
            str += printTVe("Giảm còn: ", amount_deduction + " Đ/Tháng\n");
        } else {
            str += printTVe("Giá tiền: ", price + "\n");
            str += printTVe("Chiết khấu: ", "\n");
            str += printTVe("Giảm còn: ", "\n");
        }
        if (!limited.equals("null")) {
            str += printTCard("Số lượt: " + "\t", limited +"  lượt/tháng"+ "\n");
        }
        str += printeTextCard("(Giá vé đã bao gồm bảo hiểm hành khách)" + " \n");
        str += prinTextCard("In lúc: " + time + " " + "Ngày: " + date + "\n");
        str += PrintBoldAndCleanBoldVe("Thời hạn SD: ", giahan +"    ", "Hotline: " ,phone+ "\n");
        try {
            textbytes = str.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(textbytes);
                    send(new byte[]{0x1d, 0x0c});
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                }
            }
        }, 500);
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
    }
    @ReactMethod
    public void UnregisterReceiver(){
        // Log.e("DEVK: ", "onHostDestroy");
        mCommonApi.setGpioOut(58, 0);
        mCommonApi.closeCom(mComFd);
    }
    @Override
    public void onHostDestroy() {
        mCommonApi.setGpioOut(58, 0);
        mCommonApi.closeCom(mComFd);
    }



    private String byteToString(byte[] b, int size) {
        byte high, low;
        byte maskHigh = (byte) 0xf0;
        byte maskLow = 0x0f;

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size; i++) {
            high = (byte) ((b[i] & maskHigh) >> 4);
            low = (byte) (b[i] & maskLow);
            buf.append(findHex(high));
            buf.append(findHex(low));
            buf.append(" ");
        }
        return buf.toString();
    }
    private char findHex(byte b) {
        int t = new Byte(b).intValue();
        t = t < 0 ? t + 16 : t;
        if ((0 <= t) && (t <= 9)) {
            return (char) (t + '0');
        }
        return (char) (t - 10 + 'A');
    }

    private String prinTextVe(final String str) {
        return str;
    }
    private String printTextBoldVe(final String sstr) {
        return new String(blodMax) + new String(center) + sstr + new String(noBlod) + new String(noCenter);
    }
    private String printTextBoldMinCompany(final String sstr) {
        return new String(blodMin) + new String(center) + sstr + new String(noBlod) + new String(noCenter);
    }

    private String printTextCenterVe(final String sstr) {
        return new String(center) + sstr + new String(noCenter);
    }
    private String printTVe(String str1, String str2) {
        return str1 + "   " + new String(blodMin) + str2 + new String(noBlod);
    }
    private String PrintBoldAndCleanBoldVe(String s1, String s2, String s3, String s4) {
        return s1 + new String(blodMin) + s2 + new String(noBlod) + s3 + new String(blodMin) + s4 + new String(noBlod);
    }
    private String prinTextTongKet(String str) {
        return str;
    }
    private String printTextBoldTongKet(String sstr) {
        return new String(blodMax) + new String(center) + sstr + new String(noBlod) + new String(noCenter);
    }
    private String printBoldAllTongKet(String str1, String str2) {
        return new String(blodMax) + str1 + str2 + new String(noBlod);
    }
    private String printTextCenterTongKet(final String sstr) {
        return new String(center) + sstr + new String(noCenter);
    }
    private String printeTextCard(String str) {
        return new String(center) + str + new String(noCenter);
    }
    private String prinTextCard(String str) {
        return str;
    }
    private String printTextCenterCard(final String sstr) {
        return new String(center) + sstr + new String(noCenter);
    }
    private String printTextBoldCard(String sstr) {
        return new String(blodMax) + new String(center) + sstr + new String(noBlod) + new String(noCenter);
    }
    private String printTCard(String str1, String str2) {
        return str1 + new String(blodMin) + str2 + new String(noBlod);
    }
}
