# SmartBus_Realm

Dach sách các bảng database:

- Allocation: chứa số lượng vé đang có của từng loại vé
- Bus_Station: chứa toàn bộ dữ liệu các trạm của cty
- Company: chứa thông tin cty
- Denomination: chứa thông tin các loại vé hàng
- Module: chứa các module đang được kích hoạt của cty
- Route_Bus_Station: chứa ID các trạm tương ứng với ID các tuyến
- Routes: chứa các tuyến xe của cty
- Setting_Global: chứa các thiết lập chung của cty
- Ticket_Type: chứa thông tin các loại vé của cty
- Transaction: chứa toàn bộ các giao dịch phát sinh trong 1 ca hoạt động - được xóa đi toàn bộ khi kết ca
- User: chứa thông tin của các nhân viên trong cty (tài xế, phụ xe, giám sát, ..)
- Vehicle: chứa thông tin các xe của cty
- Activity: chứa toàn bộ các hoạt động offline của người dùng - được xóa đi khi activity tương ứng đã được upload lên server

Quy trình bán vé :

- Chọn vé
- Kiểm tra lượng vé còn - nếu số lượng gần hết (<100) gọi API để server cấp thêm vé
- Tính toàn các mức khấu trừ nếu giao dịch dùng bằng thẻ
- Lưu thông tin giao dịch vào bảng transaction
- Lưu thông tin giao dịch vào bảng activity
- Gọi API để upload toàn bộ các activity lên server - nếu có lỗi(timeout ,...) thử lại 5 lần. Quá 5 lần chuyển sang bước tiêp theo
- In vé

Quy trình cấp vé :

- Kiểm tra số lưong còn lại của vé
