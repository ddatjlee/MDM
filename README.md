# MDM
VI:

Đây là app điện thoại dùng để theo dõi hành vi chụp ảnh của người dùng , nó sẽ lưu vị trí và hình ảnh của người chụp gửi vào server
Trong ứng dụng tôi thiết kế để nó gửi hình ảnh vào server Local trong máy . chỉ cần tải thêm xampp , rồi mở server Apache của xampp lên là được.
Sau đó bạn có thể vào ứng dụng và thay đổi thành ip của bạn là tất cả hình ảnh được chụp đều sẽ chuyển về server Local.

Do đây là sản phẩm đầu tiên của tôi nên nó còn nhiều thiếu sót như sau : 
+ Giữa điện thoại và server phải kết nối cùng 1 mạng internet thì mới có thể gửi hình.
+ Không có tính năng up video lên server.
+ Nếu người dùng cố tình tắt gps khi chụp ảnh thì ảnh vẫn sẽ gửi lên server được nhưng nó sẽ không hiển thị vị trí.
+ Đây đáng ra là ứng dụng chạy ngầm nhưng mà nó yêu cầu phải chụp ảnh bằng camera trong app MDM thì mới có thể gửi hình được.
+ Nó không tự lưu vào hàng chờ để tải lên server nếu người dùng cố tình tắt mạng đi.

Hướng cải thiện :
+ Nên dùng các server như Firebase hoặc MongoDB để server luôn luôn hoạt động và không ép buộc phải dùng chung mạng internet để tải ảnh lên server.
+ Đọc các "Tín hiệu chụp ảnh" để có thể tự động up toàn bộ ảnh , video từ nhiều các ứng dụng chụp ảnh đẹp khác như Camera mặc định, B612, Ulike .....
+ Giải quyết các vấn đề như nếu tắt internet , vị trí thì nó sẽ lưu vào Queue hay gì đó để khi mở lên lại thì sẽ tự động được up lên server

Do đây là sản phẩm đầu tiên tôi tạo ra khi đầu năm 3 đại học nên chắc chắn sẽ còn nhiều thứ có thể được nâng cấp lên được. Nếu như ai muốn sử dụng sản phẩm của tôi vì mục đích học tập , công việc , hay gì đó thì cứ thoải mái . Tôi chỉ yêu cầu các bạn khi sử dụng để nguồn tham khảo và nhắn tin cho tôi biết thôi. 

English:

This is a mobile application designed to monitor photo-taking behavior. It records the user's location and captured images, sending them to a local server.

In this application, I have configured it to send images to a local server hosted on your computer. All you need to do is download XAMPP, start the Apache server, and configure the app with your IP address. Once set up, all captured images will be sent to your local server.

Since this is my first project, it still has several limitations, as follows:
+ Both the mobile device and the server must be connected to the same network for images to be sent successfully.
+ The app does not support uploading videos to the server.
+ If users intentionally disable GPS while taking photos, the images will still be sent to the server, but no location data will be included.
+ The app was intended to run in the background but currently requires users to take photos using the MDM app's camera to upload them to the server.
+ There is no functionality to queue uploads when the network is unavailable. If the user disables the internet, images will not be saved for later upload.
  
Potential Improvements:
+ Use servers like Firebase or MongoDB to ensure the server is always online and not restricted to the same network as the mobile device.
+ Detect "photo-taking signals" to automatically upload all images and videos from various camera apps, such as the default Camera app, B612, or Ulike.
+ Address issues like disabled internet or GPS by saving data in a queue so that it can be automatically uploaded when the connection is restored.

This is my first project, developed in the third year of university, so there is undoubtedly room for improvement. If anyone wants to use this application for study, work, or any other purpose, feel free to do so. I only ask that you credit me as a reference and inform me if you use it.
