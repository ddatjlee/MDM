<?php
// Set the default time zone to Vietnam (Asia/Ho_Chi_Minh)
date_default_timezone_set('Asia/Ho_Chi_Minh');

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $uploadDir = 'uploads/';
    $uploadFile = $uploadDir . basename($_FILES['image']['name']);

    // Try to move the uploaded file to the uploads directory
    if (move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile)) {
        // Get the latitude and longitude from the POST request
        $latitude = $_POST['latitude'];
        $longitude = $_POST['longitude'];

        // Get the current timestamp using the set time zone
        $uploadTime = date("Y-m-d H:i:s");

        // Prepare the location data to be saved in locations.txt
        $locationFile = 'locations.txt';
        $locationData = "$latitude,$longitude,$uploadTime\n";

        // Append the location data to the file
        file_put_contents($locationFile, $locationData, FILE_APPEND);

        // Create the image URL to return in the response
        $imageURL = 'http://' . $_SERVER['HTTP_HOST'] . '/' . $uploadFile;

        // Respond with JSON indicating success, and include the image URL and upload time
        echo json_encode([
            'status' => 'success',
            'message' => 'File uploaded successfully.',
            'imageURL' => $imageURL,  // URL to view the image
            'uploadTime' => $uploadTime  // Timestamp of the upload
        ]);
    } else {
        // Respond with JSON indicating failure to upload the file
        echo json_encode(['status' => 'error', 'message' => 'Failed to upload file.']);
    }
} else {
    // Respond with JSON indicating an invalid request method
    echo json_encode(['status' => 'error', 'message' => 'Invalid request.']);
}
?>