package com.smartbus_realm.qs408;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;
import android.zyapi.CommonApi;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
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
    MBroadcastReceiver mBroadcastReceiver;
    PrintBroadcastReceiver printBroadcastReceiver;
    public PrintModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }
    @Override
    public String getName() {
        return "PrintModule";
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
        mBroadcastReceiver = new MBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("NOPAPER");
        context.registerReceiver(mBroadcastReceiver, intentFilter);
        printBroadcastReceiver = new PrintBroadcastReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("PRINTSUCCESS");
        context.registerReceiver(printBroadcastReceiver, intentFilter1);
    }
    public static void open() {
        send(new byte[]{0x1B, 0x23, 0x23, 0x35, 0x36, 0x55, 0x50});
    }
    public void initGPIO() {
        mCommonApi = new CommonApi();
        mComFd = mCommonApi.openCom("/dev/ttyMT3", 115200, 8, 'N', 1);
        if (mComFd > 0) {
            isOpen = true;
            Toast.makeText(getReactApplicationContext(), "init success", Toast.LENGTH_SHORT).show();
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
    @ReactMethod
    public void printTicketGoodsBitmap(String company, String address, String phone,
                                       String mst, String kv, String ts, String tram,
                                       String nv, String gia, String ngay, String order_good, final Promise promise) {

        String str1 = company;
        StringBuffer sb = new StringBuffer();
        sb.append(address + "\n");
        sb.append("ĐT: " + phone + "\t\t\t\t\t\t\t MST：" + mst + "\n");
        String merge_str = sb.toString();

        String str5 = kv.toUpperCase();

        StringBuffer sup1 = new StringBuffer();
        sup1.append("(Liên 1: Trên gói hàng)\n");


        Bitmap btm_name1 = textAsBitmap1(str1.toUpperCase(), 600, 21);
        Bitmap btm_name2 = textAsBitmap1(merge_str, 600, 20);
        Bitmap btm_name1_2 = twoBtmap2One(btm_name1, btm_name2);

        Bitmap btm_sup1 = textAsBitmap2(sup1.toString(), 600, 20);

        Bitmap btm_name3 = textAsBitmapBold(str5, 600, 32, BOLD_CENTER);
        Bitmap btm_merge_sup1 = twoBtmap2One(btm_name3, btm_sup1);
        Bitmap str_bitmap = twoBtmap2One(btm_name1_2, btm_merge_sup1);

        Bitmap sub1_tuyenSo = textAsBitmap1("Tên người gửi: _________________________", 600, 20);

        Bitmap str_bitmap_t_tram = twoBtmap2One(str_bitmap, sub1_tuyenSo);

        Bitmap sdt_nguoigui = textAsBitmap1("SĐT: _________________________", 600, 20);

        Bitmap str_bitmap_t_nv = twoBtmap2One(str_bitmap_t_tram, sdt_nguoigui);

        Bitmap sub1_tramgui = textAsBitmap1("Trạm gửi: _________________________", 600, 20);

        Bitmap str_bitmap_tram_gui = twoBtmap2One(str_bitmap_t_nv, sub1_tramgui);


        Bitmap sub1_nguoinhan = textAsBitmap1("Tên người nhận: _________________________", 600, 20);

        Bitmap str_bitmap_nguoinhan = twoBtmap2One(str_bitmap_tram_gui, sub1_nguoinhan);

        Bitmap sdt_nguoinhan = textAsBitmap1("SĐT: _________________________", 600, 20);

        Bitmap str_bitmap_gui_nhan = twoBtmap2One(str_bitmap_nguoinhan, sdt_nguoinhan);

        Bitmap sub1_tramnhan = textAsBitmap1("Trạm nhận: _________________________", 600, 20);

        Bitmap str_bitmap_tram_gui_nhan = twoBtmap2One(str_bitmap_gui_nhan, sub1_tramnhan);


        Bitmap sub1_gia1 = textAsBitmap1("Phí gửi: ", 80, 24);
        Bitmap btm_gia2 = textAsBitmapBold(gia + " VNĐ", 520, 27, BOLD_NORMAL);
        Bitmap btm_merge_gia = twoBtmap2One1(sub1_gia1, btm_gia2);

        Bitmap str_bitmap_t_gia = twoBtmap2One(str_bitmap_tram_gui_nhan, btm_merge_gia);

        Bitmap sub1_baohiem = textAsBitmap1("\t\t\t\t(Giá vé đã bao gồm bảo hiểm hành khách)", 600, 20);

        Bitmap str_bitmap_t_baohiem = twoBtmap2One(str_bitmap_t_gia, sub1_baohiem);


        Bitmap sub1_inNgay1 = textAsBitmap1("In ngày: ", 90, 24);
        Bitmap btm_inNgay2 = textAsBitmapBold(ngay, 510, 26, BOLD_NORMAL);
        Bitmap btm_merge_inNgay = twoBtmap2One1(sub1_inNgay1, btm_inNgay2);

        Bitmap str_bitmap_t_inNgay = twoBtmap2One(str_bitmap_t_baohiem, btm_merge_inNgay);

        str_bitmap = newBitmap(str_bitmap_t_inNgay);
        final byte[] b = draw2PxPoint(str_bitmap);
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(b);
                    send(new byte[]{0x1d, 0x0c});

                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});

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
    public void printTicket(String company, String address, String phone,
                            String mst, String ms, String kh, String sv, String kv, String ts,
                            String tram, String nv, String gia, String ngay, final Promise promise) {

        String str1 = company;
        StringBuffer sb = new StringBuffer();
        sb.append(address + "\n");
        sb.append("ĐT: " + phone + "\t\t\t\t\t\t\t MST：" + mst + "\n");
        sb.append(("Mẫu số：" + ms + "\t\t\t\t\t\t Ký hiệu：" + kh + "\t\t\t\t\t\n"));
        sb.append("Số vé: " + sv + "\t");
        String merge_str = sb.toString();

        String str5 = kv.toUpperCase();

        StringBuffer sup1 = new StringBuffer();
        sup1.append("(Liên 2: Giao cho khách hàng)\n");


        Bitmap btm_name1 = textAsBitmap1(str1.toUpperCase(), 600, 21);
        Bitmap btm_name2 = textAsBitmap1(merge_str, 600, 20);
        Bitmap btm_name1_2 = twoBtmap2One(btm_name1, btm_name2);

        Bitmap btm_sup1 = textAsBitmap2(sup1.toString(), 600, 20);

        Bitmap btm_name3 = textAsBitmapBold(str5, 600, 32, BOLD_CENTER);
        Bitmap btm_merge_sup1 = twoBtmap2One(btm_name3, btm_sup1);
        Bitmap str_bitmap = twoBtmap2One(btm_name1_2, btm_merge_sup1);


        Bitmap sub1_tuyenSo = textAsBitmap1("Tuyến số: " + ts, 170, 24);


        Bitmap sub1_tram1 = textAsBitmap1("Trạm: ", 70, 24);
        Bitmap btm_tram2 = textAsBitmapBold(tram, 360, 27, BOLD_NORMAL);
        Bitmap btm_merge_tr = twoBtmap2One1(sub1_tram1, btm_tram2);

        Bitmap btm_merge_ts_tr = twoBtmap2One1(sub1_tuyenSo, btm_merge_tr);

        Bitmap str_bitmap_t_tram = twoBtmap2One(str_bitmap, btm_merge_ts_tr);

        Bitmap sub1_nv1 = textAsBitmap1("NV: ", 50, 24);
        Bitmap sub1_nv2 = textAsBitmap1(nv, 550, 26);
        Bitmap btm_merge_nv = twoBtmap2One1(sub1_nv1, sub1_nv2);

        Bitmap str_bitmap_t_nv = twoBtmap2One(str_bitmap_t_tram, btm_merge_nv);

        Bitmap sub1_gia1 = textAsBitmap1("Giá vé: ", 80, 24);
        Bitmap btm_gia2 = textAsBitmapBold(gia + " VNĐ/Lượt", 520, 27, BOLD_NORMAL);
        Bitmap btm_merge_gia = twoBtmap2One1(sub1_gia1, btm_gia2);

        Bitmap str_bitmap_t_gia = twoBtmap2One(str_bitmap_t_nv, btm_merge_gia);

        Bitmap sub1_baohiem = textAsBitmap1("\t\t\t\t(Giá vé đã bao gồm bảo hiểm hành khách)", 600, 20);

        Bitmap str_bitmap_t_baohiem = twoBtmap2One(str_bitmap_t_gia, sub1_baohiem);


        Bitmap sub1_inNgay1 = textAsBitmap1("In ngày: ", 90, 24);
        Bitmap btm_inNgay2 = textAsBitmapBold(ngay, 510, 26, BOLD_NORMAL);
        Bitmap btm_merge_inNgay = twoBtmap2One1(sub1_inNgay1, btm_inNgay2);

        Bitmap str_bitmap_t_inNgay = twoBtmap2One(str_bitmap_t_baohiem, btm_merge_inNgay);

        Bitmap btm_inTai = textAsBitmap1("In tại: " + str1.toUpperCase(), 600, 19);

        Bitmap str_bitmap_t_inTai = twoBtmap2One(str_bitmap_t_inNgay, btm_inTai);

        Bitmap sub1_mst = textAsBitmap1("MST: " + mst + "\n", 600, 20);

        Bitmap str_bitmap_t_mst = twoBtmap2One(str_bitmap_t_inTai, sub1_mst);

        str_bitmap = newBitmap(str_bitmap_t_mst);
        final byte[] b = draw2PxPoint(str_bitmap);
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(b);
                    send(new byte[]{0x1d, 0x0c});

                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});

                }
            }
        }, 500);

    }
    @ReactMethod
    public void printTicketChargeFree(String company, String address, String phone, String tax_code, String number, String nameStation, String fullname,
                                      String fullnamecustomer, String time, String expiration_date) {
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
    public void printTicketMonthQnic(String part2, String company, String address, String phone,
                                     String mst, String kv, String ts,String tram,
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
        str += printTVe("Chủ thẻ: ", tenKH + "\n");
        if (!tram1.equals("null")) {
            str += printTVe("Trạm đầu: ", tram1 + "\n");
        }
        if (!tram2.equals("null")) {
            str += printTVe("Trạm cuối: ", tram2 + "\n");
        }
        if (!charge_limit.equals("null")) {
            str += printTVe("Số lần quẹt thẻ: ", charge_limit + "\n");
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
    public void printTicketDeduction(String company, String address, String phone,
                                     String mst, String ms, String kh, String sv, String kv, String ts,
                                     String tram, String nv, String gia, String ngay, String deduction, String discount, String sodu, String intai, final Promise promise) {

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
    public void printTicketDeductionALL(String vat, String part2, String company, String address, String phone,
                                        String mst, String ms, String kh, String sv, String kv, String ts,
                                        String tram, String tram_ke, String bsx, String driver_name, String subdriver_name, String gia, String ngay, String deduction, String discount, String sodu, String intai, String the_tra_truoc, final Promise promise) {

        String str = "\n";
        str += printTextBoldMinCompany(company + "\n");
        str += prinTextVe(address + "\n");
        str += prinTextVe(mst + " \t" + ms + "\n");
        str += prinTextVe(sv + " \t" + kh + "\n");

        str += printTextBoldVe(kv + "\n");

        str += printTextCenterVe(part2 + "\n");
        str += PrintBoldAndCleanBoldVe("Tuyến số: ", ts + " ", "         BSX: ", bsx +"\n");
        str += prinTextVe(tram + "\n");
        str += prinTextVe(tram_ke + "\n");
        if (!deduction.equals("null")) {
            str += PrintBoldAndCleanBoldVe("Giá vé: ", gia, "  Chiết khấu: ", discount + " Đ/Lượt" + "\n");
            str += printTVe("Giảm còn: ", deduction + " Đ/Lượt" + "\n");
        } else {
            if (gia.equals("NaN")) {
                str += PrintBoldAndCleanBoldVe("Giá vé: ", "miễn phí " + " Đ/Lượt", "  Chiết khấu: ", "\n");
            } else {
                str += PrintBoldAndCleanBoldVe("Giá vé: ", gia , "  Chiết khấu: ", "\n");
            }
            str += printTVe("Giảm còn: ", "\n");
        }
        str += printTextCenterVe(vat + " \n");
        str += printTVe("In ngày: ", ngay + " " + "\n");

        if (!sodu.equals("null")) {
            str += PrintBoldAndCleanBoldVe("Số dư thẻ: ", sodu + " Đ", " \t" + "Hotline: " + phone, "\n\n");
        } else {
            if (the_tra_truoc.equals("true")) {
                str += prinTextVe("Số dư thẻ: " + " \t\t" + "Hotline: " + phone + "\n\n");
            } else {
                str += prinTextVe("Hotline: " + phone + "\n\n");
            }
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
    public void printTicketDeduction1(String company, String address, String phone,
                                      String mst, String ms, String kh, String sv, String kv, String ts,
                                      String tram, String nv, String gia, String ngay, String deduction, String sodu, final Promise promise) {
        String str1 = company;
        StringBuffer sb = new StringBuffer();
        sb.append(address + "\n");
        sb.append("ĐT: " + phone + "\t\t\t\t\t\t\t MST：" + mst + "\n");
        sb.append(("Mẫu số：" + ms + "\t\t\t\t\t\t Ký hiệu：" + kh + "\t\t\t\t\t\n"));
        sb.append("Số vé: " + sv + "\t");
        String merge_str = sb.toString();

        String str5 = kv.toUpperCase();

        StringBuffer sup1 = new StringBuffer();
        sup1.append("(Liên 2: Giao cho khách hàng)\n");

        Bitmap btm_name1 = textAsBitmap1(str1.toUpperCase(), 600, 21);
        Bitmap btm_name2 = textAsBitmap1(merge_str, 600, 20);
        Bitmap btm_name1_2 = twoBtmap2One(btm_name1, btm_name2);

        Bitmap btm_sup1 = textAsBitmap2(sup1.toString(), 600, 20);

        Bitmap btm_name3 = textAsBitmapBold(str5, 600, 32, BOLD_CENTER);
        Bitmap btm_merge_sup1 = twoBtmap2One(btm_name3, btm_sup1);
        Bitmap str_bitmap = twoBtmap2One(btm_name1_2, btm_merge_sup1);

        Bitmap sub1_tuyenSo = textAsBitmap1("Tuyến số: " + ts, 170, 24);


        Bitmap sub1_tram1 = textAsBitmap1("Trạm: ", 70, 24);
        Bitmap btm_tram2 = textAsBitmapBold(tram, 360, 27, BOLD_NORMAL);
        Bitmap btm_merge_tr = twoBtmap2One1(sub1_tram1, btm_tram2);

        Bitmap btm_merge_ts_tr = twoBtmap2One1(sub1_tuyenSo, btm_merge_tr);

        Bitmap str_bitmap_t_tram = twoBtmap2One(str_bitmap, btm_merge_ts_tr);

        Bitmap sub1_nv1 = textAsBitmap1("NV: ", 50, 24);
        Bitmap sub1_nv2 = textAsBitmap1(nv, 550, 26);
        Bitmap btm_merge_nv = twoBtmap2One1(sub1_nv1, sub1_nv2);

        Bitmap str_bitmap_t_nv = twoBtmap2One(str_bitmap_t_tram, btm_merge_nv);

        Bitmap sub1_gia1 = textAsBitmap1("Giá vé: ", 80, 24);
        Bitmap btm_gia2 = textAsBitmapBold(gia + " VNĐ/Lượt", 520, 27, BOLD_NORMAL);
        Bitmap btm_merge_gia = twoBtmap2One1(sub1_gia1, btm_gia2);

        Bitmap str_bitmap_t_gia = twoBtmap2One(str_bitmap_t_nv, btm_merge_gia);


        Bitmap sub1_giamcon1 = textAsBitmap1("Giảm còn:", 130, 26);
        Bitmap sub1_giamcon2 = textAsBitmapBold(deduction + " VNĐ/Lượt", 470, 27, BOLD_NORMAL);
        Bitmap sub1_giamcon = twoBtmap2One1(sub1_giamcon1, sub1_giamcon2);


        Bitmap str_bitmap_t_giam_con = twoBtmap2One(str_bitmap_t_gia, sub1_giamcon);

        Bitmap sub1_baohiem = textAsBitmap1("\t\t\t\t(Giá vé đã bao gồm bảo hiểm hành khách)", 600, 20);

        Bitmap str_bitmap_t_baohiem = twoBtmap2One(str_bitmap_t_giam_con, sub1_baohiem);


        Bitmap sub1_inNgay1 = textAsBitmap1("In ngày: ", 90, 24);
        Bitmap btm_inNgay2 = textAsBitmapBold(ngay, 510, 26, BOLD_NORMAL);
        Bitmap btm_merge_inNgay = twoBtmap2One1(sub1_inNgay1, btm_inNgay2);

        Bitmap str_bitmap_t_inNgay = twoBtmap2One(str_bitmap_t_baohiem, btm_merge_inNgay);

        Bitmap btm_inTai = textAsBitmap1("In tại: " + str1.toUpperCase(), 600, 19);

        Bitmap str_bitmap_t_inTai = twoBtmap2One(str_bitmap_t_inNgay, btm_inTai);

        Bitmap sub1_mst = textAsBitmap1("MST: " + mst, 600, 20);

        Bitmap str_bitmap_t_mst = twoBtmap2One(str_bitmap_t_inTai, sub1_mst);

        Bitmap sub1_sodu1 = textAsBitmap1("Số dư thẻ:", 130, 26);
        Bitmap sub1_sodu2 = textAsBitmapBold(sodu + " VNĐ", 470, 27, BOLD_NORMAL);
        Bitmap btm_merge_sodu = twoBtmap2One1(sub1_sodu1, sub1_sodu2);

        Bitmap str_bitmap_t_SoDu = twoBtmap2One(str_bitmap_t_mst, btm_merge_sodu);
        str_bitmap = newBitmap(str_bitmap_t_SoDu);
        final byte[] b = draw2PxPoint(str_bitmap);
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(b);
                    send(new byte[]{0x1d, 0x0c});

                    //��ӡ5�����з���˺ֽ������ֽ���ж�ʹ�ÿ��У�
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                    // promise.resolve(isSuccess);

                }
            }
        }, 500);

    }
    @ReactMethod
    public void printTotalNotUnicode(
            String company, String address, String phone,
            String mst, String kh,
            String nvBv, String nvLx, String sx, String tuyen, String tienmat,
            String napthe, String hanghoa, String qt_hanghoa, String quetthe, String qrcode, String soluong, String thethang, String totals, String ticket, String timeDn,
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
            str += printTextBoldTongKet("PHIEU TAM THOI" + "\n");
        } else {
            str += printTextBoldTongKet("TONG KET" + "\n");
        }
        str += prinTextTongKet("Ten NV ban ve: " + nvBv + "\n");
        if (fontBoldCompany.equals("null")) {
            str += prinTextTongKet("Ten NV lai xe: " + nvLx + "\n");
        }
        str += prinTextTongKet("So xe: " + sx + "       " + "Tuyen so: " + tuyen + "\n");
        str += prinTextTongKet("Menh gia" + " | " + "Tien mat" + " | " + "The" +" | "+"Qr Code"+ " |  " + "Ve"+ "\n");
        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
        JSONArray jsonResponse = new JSONArray(ticket);
        for (int i = 0; i < jsonResponse.length(); i++) {
            if (i == jsonResponse.length() - 1) {
                str += prinTextTongKet(jsonResponse.getJSONObject(i).getString("price").toString() + " VND" + " "
                        + jsonResponse.getJSONObject(i).getString("qty").toString() + "     "
                        + jsonResponse.getJSONObject(i).getString("charge").toString() + "      "
                        + jsonResponse.getJSONObject(i).getString("qr").toString()+ "       "
                        + jsonResponse.getJSONObject(i).getString("serial").toString() + "\n");

            } else {
                str += prinTextTongKet(jsonResponse.getJSONObject(i).getString("price").toString() + " VND" + " "
                        + jsonResponse.getJSONObject(i).getString("qty").toString() + "     "
                        + jsonResponse.getJSONObject(i).getString("charge").toString() + "      "
                        + jsonResponse.getJSONObject(i).getString("qr").toString()+ "       "
                        + jsonResponse.getJSONObject(i).getString("serial").toString() + "\n");
            }
        }
        str += prinTextTongKet("- - - - - - - - - - - - - - - - - - - - - - - -" + "\n");
        str += prinTextTongKet("Tien mat: " + " \t\t " + tienmat + " VND" + "\n");
        str += prinTextTongKet("Nap the + Gia han: " + " \t " + napthe + " VND" + "\n");
        str += prinTextTongKet("Quet the: " + " \t\t " + quetthe + " VND" + "\n");
        str += prinTextTongKet("Qr Code: " + " \t\t " + qrcode + " VND" + "\n");
        if (!visibleFree.equals("null")) {
            str += prinTextTongKet("So ve mien phi: " + " \t " + soluong + "\n");
        }

        str += prinTextTongKet("So ve thang: " + " \t " + thethang + "\n");
        if(!hanghoa.equals("null")) {
            str += prinTextTongKet("Tien hang: " + " \t\t " + hanghoa+ " VND" + "\n");
        }
        str += printBoldAllTongKet("Thu: " + " \t", totals + " VND\n");
        str += prinTextTongKet("Dang nhap luc: " + timeDn + "\n");
        str += prinTextTongKet("In luc: " + hours + " " + day + "\n");
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
    public void printTotal1(
            String company, String address, String phone,
            String mst, String kh,
            String nvBv, String nvLx, String sx, String tienmat,
            String napthe, String quetthe, String totals, String ticket, String timeDn,
            String hours, String day, final Promise promise
    ) throws JSONException {
        StringBuffer sbBigTt = new StringBuffer();
        sbBigTt.append("TỔNG KẾT");

        StringBuffer sbTitle = new StringBuffer();
        sbTitle.append(company + "\n");
        sbTitle.append(address + "\n");

        Bitmap btm_title = textAsBitmap1(sbTitle.toString(), 600, 20);
        Bitmap btm_big_title = textAsBitmapBold(sbBigTt.toString(), 600, 30, BOLD_CENTER);
        Bitmap btm_merge_all_title = twoBtmap2One(btm_title, btm_big_title);

        StringBuffer sbNv = new StringBuffer();

        sbNv.append("Tên nv bán vé: " + nvBv + "\n");
        sbNv.append("Tên nv lái xe: " + nvLx + "\n");
        sbNv.append("Số xe: " + sx + "\n");

        Bitmap btm_nv = textAsBitmap1(sbNv.toString(), 600, 23);
        Bitmap merge_title_nv = twoBtmap2One(btm_merge_all_title, btm_nv);

        StringBuffer sbCol1 = new StringBuffer();
        sbCol1.append("Mệnh giá");
        StringBuffer sbCol2 = new StringBuffer();
        sbCol2.append("Tiền mặt");
        StringBuffer sbCol3 = new StringBuffer();
        sbCol3.append("Quẹt thẻ");

        Bitmap btm_col1 = textAsBitmap1(sbCol1.toString(), 400, 25);
        Bitmap btm_col2 = textAsBitmap1(sbCol2.toString(), 200, 25);
        Bitmap btm_col3 = textAsBitmap1(sbCol3.toString(), 200, 25);
        Bitmap merge_btm_col_1_2_3 = threeBtmap2One1(btm_col1, btm_col3, btm_col2);
        StringBuffer sb_line = new StringBuffer();
        sb_line.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Bitmap btm_line = textAsBitmap1(sb_line.toString(), 600, 20);

        Bitmap btm_merge_col_line = twoBtmap2One(merge_btm_col_1_2_3, btm_line);
        StringBuffer sbBottomCol1 = new StringBuffer();
        StringBuffer sbBottomCol2 = new StringBuffer();
        sbBottomCol1.append("Tiền mặt:\n");
        sbBottomCol1.append("Nạp thẻ:\n");
        sbBottomCol1.append("Quẹt thẻ:");

        sbBottomCol2.append("  " + tienmat + " VNĐ\n");
        sbBottomCol2.append("  " + napthe + " VNĐ\n");
        sbBottomCol2.append("  " + quetthe + " VNĐ");

        Bitmap btm_col1_bottom = textAsBitmap1(sbBottomCol1.toString(), 330, 20);
        Bitmap btm_col2_bottom = textAsBitmap1(sbBottomCol2.toString(), 270, 20);

        Bitmap merge_btm_col_bottom = twoBtmap2One1(btm_col1_bottom, btm_col2_bottom);
        Bitmap btm_merge_col_line_2 = twoBtmap2One(btm_line, merge_btm_col_bottom);

        StringBuffer sbPrice = new StringBuffer();
        StringBuffer sbCount1 = new StringBuffer();
        StringBuffer sbCount2 = new StringBuffer();
        JSONArray jsonResponse = new JSONArray(ticket);

        for (int i = 0; i < jsonResponse.length(); i++) {
            if (i == jsonResponse.length() - 1) {
                sbPrice.append(jsonResponse.getJSONObject(i).getString("price").toString() + " VNĐ\n");
                sbCount1.append("  " + jsonResponse.getJSONObject(i).getString("qty").toString() + "\n");
                sbCount2.append("  " + jsonResponse.getJSONObject(i).getString("charge").toString() + "\n");
            } else {
                sbPrice.append(jsonResponse.getJSONObject(i).getString("price").toString() + " VNĐ\n");
                sbCount1.append("  " + jsonResponse.getJSONObject(i).getString("qty").toString() + "\n");
                sbCount2.append("  " + jsonResponse.getJSONObject(i).getString("charge").toString() + "\n");
            }
        }

        Bitmap bitmap = textAsBitmap1(sbPrice.toString(), 400, 20);
        Bitmap bitmap1 = textAsBitmap1(sbCount1.toString(), 200, 20);
        Bitmap bitmap2 = textAsBitmap1(sbCount2.toString(), 200, 20);
        Bitmap merge_btm = threeBtmap2One1(bitmap, bitmap2, bitmap1);
        Bitmap merge_btm_col_line_1 = twoBtmap2One(btm_merge_col_line, merge_btm);
        Bitmap merge_btm_all_line = twoBtmap2One(merge_btm_col_line_1, btm_merge_col_line_2);

        StringBuffer sbThu = new StringBuffer();
        sbThu.append("Thu:\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + totals + " VNĐ");

        Bitmap btm_thu = textAsBitmapBold(sbThu.toString(), 600, 26, BOLD_NORMAL);
        Bitmap btm_merge_content_thu = twoBtmap2One(merge_btm_all_line, btm_thu);

        Bitmap btm_merge_header_content = twoBtmap2One(merge_title_nv, btm_merge_content_thu);

        StringBuffer sbFooter = new StringBuffer();
        sbFooter.append("Đăng nhập lúc: " + timeDn + "\n");
        sbFooter.append("In lúc: " + hours + "\tNgày: " + day + "\n");

        Bitmap btm_footer = textAsBitmap1(sbFooter.toString(), 600, 20);
        Bitmap btm_merge_header_content_footer = twoBtmap2One(btm_merge_header_content, btm_footer);
        btm_merge_header_content_footer = newBitmap(btm_merge_header_content_footer);
        final byte[] b = draw2PxPoint(btm_merge_header_content_footer);
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(b);
                    send(new byte[]{0x1d, 0x0c});

                    //��ӡ5�����з���˺ֽ������ֽ���ж�ʹ�ÿ��У�
                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});
                    // promise.resolve(isSuccess);

                }
            }
        }, 500);
    }
    @ReactMethod
    public void printTotal2(
            String company, String address, String phone,
            String mst, String kh,
            String nvBv, String nvLx, String sx,
            String napthe, String quetthe, String totals, String ticket, String timeDn,
            String hours, String day, final Promise promise
    ) throws JSONException {
        StringBuffer sbBigTt = new StringBuffer();
        sbBigTt.append("TỔNG KẾT");

        StringBuffer sbTitle = new StringBuffer();
        sbTitle.append(company + "\n");
        sbTitle.append(address + "\n");

        Bitmap btm_title = textAsBitmap1(sbTitle.toString(), 600, 20);
        Bitmap btm_big_title = textAsBitmapBold(sbBigTt.toString(), 600, 30, BOLD_CENTER);
        Bitmap btm_merge_all_title = twoBtmap2One(btm_title, btm_big_title);

        StringBuffer sbNv = new StringBuffer();

        sbNv.append("Tên nv bán vé: " + nvBv + "\n");
        sbNv.append("Tên nv lái xe: " + nvLx + "\n");
        sbNv.append("Số xe: " + sx + "\n");

        Bitmap btm_nv = textAsBitmap1(sbNv.toString(), 600, 23);
        Bitmap merge_title_nv = twoBtmap2One(btm_merge_all_title, btm_nv);

        StringBuffer sbCol1 = new StringBuffer();
        sbCol1.append("Mệnh giá");
        StringBuffer sbCol2 = new StringBuffer();
        sbCol2.append("Số lượng");


        Bitmap btm_col1 = textAsBitmap1(sbCol1.toString(), 400, 25);
        Bitmap btm_col2 = textAsBitmap1(sbCol2.toString(), 200, 25);
        Bitmap merge_btm_col_1_2 = twoBtmap2One1(btm_col1, btm_col2);
        StringBuffer sb_line = new StringBuffer();
        sb_line.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Bitmap btm_line = textAsBitmap1(sb_line.toString(), 600, 20);

        Bitmap btm_merge_col_line = twoBtmap2One(merge_btm_col_1_2, btm_line);
        StringBuffer sbBottomCol1 = new StringBuffer();
        StringBuffer sbBottomCol2 = new StringBuffer();
        sbBottomCol1.append("Nạp thẻ:\n");
        sbBottomCol1.append("Quẹt thẻ:");

        sbBottomCol2.append("  " + napthe + "\n");
        sbBottomCol2.append("- " + quetthe + "");

        Bitmap btm_col1_bottom = textAsBitmap1(sbBottomCol1.toString(), 400, 20);
        Bitmap btm_col2_bottom = textAsBitmap1(sbBottomCol2.toString(), 200, 20);

        Bitmap merge_btm_col_bottom = twoBtmap2One1(btm_col1_bottom, btm_col2_bottom);
        Bitmap btm_merge_col_line_2 = twoBtmap2One(btm_line, merge_btm_col_bottom);

        StringBuffer sbPrice = new StringBuffer();
        StringBuffer sbCount = new StringBuffer();
        JSONArray jsonResponse = new JSONArray(ticket);

        for (int i = 0; i < jsonResponse.length(); i++) {
            if (i == jsonResponse.length() - 1) {
                sbPrice.append(jsonResponse.getJSONObject(i).getString("price").toString() + " VNĐ");
                sbCount.append("  " + jsonResponse.getJSONObject(i).getString("qty").toString() + "");
            } else {
                sbPrice.append(jsonResponse.getJSONObject(i).getString("price").toString() + " VNĐ\n");
                sbCount.append("  " + jsonResponse.getJSONObject(i).getString("qty").toString() + "\n");
            }
        }

        Bitmap bitmap = textAsBitmap1(sbPrice.toString(), 400, 20);
        Bitmap bitmap1 = textAsBitmap1(sbCount.toString(), 200, 20);
        Bitmap merge_btm = twoBtmap2One1(bitmap, bitmap1);
        Bitmap merge_btm_col_line_1 = twoBtmap2One(btm_merge_col_line, merge_btm);
        Bitmap merge_btm_all_line = twoBtmap2One(merge_btm_col_line_1, btm_merge_col_line_2);

        StringBuffer sbThu = new StringBuffer();
        sbThu.append("Thu:\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + totals + " VNĐ");

        Bitmap btm_thu = textAsBitmapBold(sbThu.toString(), 600, 26, BOLD_NORMAL);
        Bitmap btm_merge_content_thu = twoBtmap2One(merge_btm_all_line, btm_thu);

        Bitmap btm_merge_header_content = twoBtmap2One(merge_title_nv, btm_merge_content_thu);

        StringBuffer sbFooter = new StringBuffer();
        sbFooter.append("Đăng nhập lúc: " + timeDn + "\n");
        sbFooter.append("In lúc: " + hours + "\tNgày: " + day + "\n");

        Bitmap btm_footer = textAsBitmap1(sbFooter.toString(), 600, 20);
        Bitmap btm_merge_header_content_footer = twoBtmap2One(btm_merge_header_content, btm_footer);
        btm_merge_header_content_footer = newBitmap(btm_merge_header_content_footer);
        final byte[] b = draw2PxPoint(btm_merge_header_content_footer);
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(b);
                    send(new byte[]{0x1d, 0x0c});

                    //��ӡ5�����з���˺ֽ������ֽ���ж�ʹ�ÿ��У�
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
    @ReactMethod
    public void printCardBitMap(
            String company, String address, String phone,
            String mst, String kh, String mt, String tenct, String nv, String price, String sum,
            String time, String date, final Promise promise
    ) {
        String bigTitle = "HOÁ ĐƠN THANH TOÁN";
        StringBuffer sbTitle = new StringBuffer();
        sbTitle.append(company + "\n");
        sbTitle.append("" + address + "\n");
        sbTitle.append("ĐT: " + phone + "\n");
        sbTitle.append("Ký hiệu：" + kh + "\t\t\t\t" + "MST: " + mst + "\n");

        Bitmap btm_title = textAsBitmap1(sbTitle.toString(), 600, 20);
        Bitmap btm_bigTitle = textAsBitmapBold(bigTitle, 600, 30, BOLD_CENTER);
        Bitmap btm_merge_title_bigtTitle = twoBtmap2One(btm_title, btm_bigTitle);

        StringBuffer sb1 = new StringBuffer();
        sb1.append("Nạp thẻ trả trước\n");
        StringBuffer sb3 = new StringBuffer();
        sb3.append("Mã thẻ:\t\t\t\t\t\t" + mt);
        StringBuffer sb4 = new StringBuffer();
        sb4.append("Tên chủ thẻ:\t\t\t\t" + tenct);

        Bitmap btm_sb1 = textAsBitmap1("\t\t\t\t\t\t\t\t\t\t" + sb1.toString(), 600, 25);
        Bitmap btm_merge_t = twoBtmap2One(btm_merge_title_bigtTitle, btm_sb1);

        Bitmap btm_sb2 = textAsBitmap1(sb3.toString(), 600, 25);
        Bitmap btm_merge_t_mathe = twoBtmap2One(btm_merge_t, btm_sb2);

        Bitmap btm_sb3 = textAsBitmap1(sb4.toString(), 600, 25);
        Bitmap btm_merge_t_tenct = twoBtmap2One(btm_merge_t_mathe, btm_sb3);

        Bitmap sub1_btm = textAsBitmap1("Giá tiền:", 200, 25);
        Bitmap btm_price = textAsBitmapBold(price + " VNĐ", 400, 25, BOLD_NORMAL);
        Bitmap btm_merge_sub1_price = twoBtmap2One1(sub1_btm, btm_price);

        Bitmap btm_merge_t_gia = twoBtmap2One(btm_merge_t_tenct, btm_merge_sub1_price);


        Bitmap sub2_btm = textAsBitmap1("NV:", 200, 25);
        Bitmap btm_nv = textAsBitmapBold(nv, 400, 25, BOLD_NORMAL);
        Bitmap btm_merge_sub2_price = twoBtmap2One1(sub2_btm, btm_nv);

        Bitmap btm_merge_t_NV = twoBtmap2One(btm_merge_t_gia, btm_merge_sub2_price);

        Bitmap sub3_btm = textAsBitmap1("Tổng số dư:", 200, 25);
        Bitmap btm_sodu = textAsBitmapBold(sum + " VNĐ", 400, 25, BOLD_NORMAL);
        Bitmap btm_merge_sub3_sodu = twoBtmap2One1(sub3_btm, btm_sodu);

        Bitmap btm_merge_t_sodu = twoBtmap2One(btm_merge_t_NV, btm_merge_sub3_sodu);

        StringBuffer sb2 = new StringBuffer();
        sb2.append("Mua lúc: " + time + "\t Ngày: " + date + "\n");
        Bitmap btm_sub2 = textAsBitmap1(sb2.toString(), 600, 25);

        Bitmap btm_merge_all = twoBtmap2One(btm_merge_t_sodu, btm_sub2);

        btm_merge_all = newBitmap(btm_merge_all);

        final byte[] b = draw2PxPoint(btm_merge_all);
        send(new byte[]{0x1D, 0x23, 0x53, (byte) 0xD1, 0x7A, (byte) 0xF8, 0x4d});
        send(new byte[]{0x1d, 0x61, 0x00});
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isCanprint) {
                    send(b);
                    send(new byte[]{0x1d, 0x0c});

                    send(new byte[]{0x0a, 0x0a, 0x0a, 0x0a});
                    send(new byte[]{0x1D, 0x23, 0x45});

                }
            }
        }, 500);


    }
    private Bitmap newBitmap(Bitmap bit1) {
        int width = bit1.getWidth();
        int height = bit1.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit1, 0, 0, null);
        return bitmap;
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
        getReactApplicationContext().unregisterReceiver(mBroadcastReceiver);
        getReactApplicationContext().unregisterReceiver(printBroadcastReceiver);
        mCommonApi.setGpioOut(58, 0);
        mCommonApi.closeCom(mComFd);
    }
    @Override
    public void onHostDestroy() {
        mCommonApi.setGpioOut(58, 0);
        mCommonApi.closeCom(mComFd);
    }
    public static void send(byte[] data) {
        if (data == null)
            return;
        if (mComFd > 0) {
            mCommonApi.writeCom(mComFd, data, data.length);
        }
    }
    public Bitmap threeBtmap2One1(Bitmap bitmap1, Bitmap bitmap2, Bitmap bitmap3) {
        Bitmap bitmap4 = Bitmap.createBitmap(
                bitmap1.getWidth() + bitmap2.getWidth() + bitmap3.getWidth(), bitmap2.getHeight(),
                bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap4);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth(), 0, null);
        canvas.drawBitmap(bitmap3, bitmap2.getWidth(), 0, null);
        return bitmap4;
    }

    public Bitmap twoBtmap2One1(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bitmap3 = Bitmap.createBitmap(
                bitmap1.getWidth() + bitmap2.getWidth(), bitmap2.getHeight(),
                bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth(), 0, null);
        return bitmap3;
    }

    public Bitmap twoBtmap2One(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(),
                bitmap1.getHeight() + bitmap2.getHeight() - 4, bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap3);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
        return bitmap3;
    }

    public static Bitmap textAsBitmap1(String text, int width, float textSize) {

        TextPaint textPaint = new TextPaint();

        textPaint.setColor(Color.BLACK);

        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        StaticLayout layout = new StaticLayout(text, textPaint, width,
                Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);

        layout.draw(canvas);
        return bitmap;

    }

    public static Bitmap textAsBitmapBold(String text, int width, float textSize, Layout.Alignment position) {

        TextPaint textPaint = new TextPaint();

        textPaint.setColor(Color.BLACK);

        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        StaticLayout layout = layout = new StaticLayout(text, textPaint, width,
                position, 1.3f, 0.0f, true);
        ;

        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight() + 15, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);

        layout.draw(canvas);
        return bitmap;

    }


    public static Bitmap textAsBitmap2(String text, int width, float textSize) {

        TextPaint textPaint = new TextPaint();

        textPaint.setColor(Color.BLACK);

        textPaint.setTextSize(textSize);

        StaticLayout layout = new StaticLayout(text, textPaint, width,
                Layout.Alignment.ALIGN_CENTER, 1.3f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(),
                layout.getHeight() + 7, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);

        layout.draw(canvas);
        return bitmap;

    }

    public static byte[] draw2PxPoint(Bitmap bmp) {
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 2200;
        byte[] data = new byte[size];
        int k = 0;
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); // nL
            data[k++] = (byte) (bmp.getWidth() / 256); // nH
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int m = 0; m < 3; m++) {
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        if (k < size) {
                            data[k] += data[k] + b;
                        }
                    }
                    k++;
                }
            }
            if (k < size) {
                data[k++] = 10;// 换行
            }
        }
        return data;
    }
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }
    class MBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.e(DEVK, "noPaper !!!");
            noPaper();
        }
    }

    class PrintBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            processPrint();
        }
    }
    private void readData() {
        new Thread() {
            public void run() {
                while (isOpen) {
                    try{
                        //  Log.e("DEVK", "Chay 1read success:");
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
                            // Log.d("DEVK no paper", "no paper");
                            Intent mIntent = new Intent("NOPAPER");
                            context.sendBroadcast(mIntent);
                        } else {
                            isCanprint = true;
                        }
                        if (str.contains("4D")) { //1D 42 45 D1 7A F8
                            isCanprint = true;
                            Intent i = new Intent("PRINTSUCCESS");
                            context.sendBroadcast(i);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        }.start();
    }
    public void processPrint() {
        WritableMap params;
        params = Arguments.createMap();
        params.putString("status", "success");
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("PRINT_PROCESS", params);
    }
    public void noPaper() {
        WritableMap params;
        params = Arguments.createMap();
        params.putString("status", "nopaper");
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("NO_PAPER", params);
    }
    public String byteToString(byte[] b, int size) {
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
