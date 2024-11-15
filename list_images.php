<?php
// Set headers for JSON response
header('Content-Type: application/json');

// Set the default time zone to Vietnam (Asia/Ho_Chi_Minh)
date_default_timezone_set('Asia/Ho_Chi_Minh');

// Define the directory where uploads are stored
$dir = 'uploads/';
$files = scandir($dir);

// Get the base URL of the current request
$baseURL = 'http://' . $_SERVER['HTTP_HOST'] . dirname($_SERVER['SCRIPT_NAME']) . '/';

// Array to hold image info
$images = [];

// Read the location data from the locations.txt file
$locationFile = 'locations.txt';
$locationData = file($locationFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

// Initialize variables to hold the latest latitude and longitude
$latestLatitude = null;
$latestLongitude = null;

// Process the location data
foreach ($locationData as $line) {
    // Each line contains latitude and longitude
    $parts = explode(',', $line);
    if (count($parts) === 2) {
        $latestLatitude = trim($parts[0]);
        $latestLongitude = trim($parts[1]);
    }
}

foreach ($files as $file) {
    // Skip current and parent directory references
    if ($file !== '.' && $file !== '..' && preg_match('/\.(jpg|jpeg|png|gif)$/i', $file)) {
        // Get the full path of the file
        $filePath = $dir . $file;

        // Get file size in KB and MB formats
        $fileSizeBytes = filesize($filePath);
        $fileSizeKB = round($fileSizeBytes / 1024, 2);  // Size in KB
        $fileSizeMB = round($fileSizeKB / 1024, 2);     // Size in MB

        // Get the last modified timestamp of the file, now using the set time zone
        $lastModified = date('Y-m-d H:i:s', filemtime($filePath));

        // Get image dimensions (width and height)
        $imageSize = getimagesize($filePath);
        $imageWidth = $imageSize[0];
        $imageHeight = $imageSize[1];

        // Construct the accessible URL for the image
        $imageURL = $baseURL . $filePath;

        // Add file info to the images array
        $images[] = [
            'url' => $imageURL,              // URL to access the image
            'fileName' => $file,             // Name of the file
            'fileSizeKB' => $fileSizeKB,     // File size in KB
            'fileSizeMB' => $fileSizeMB,     // File size in MB
            'lastModified' => $lastModified, // Last modified timestamp of the image
            'imageWidth' => $imageWidth,     // Image width in pixels
            'imageHeight' => $imageHeight,   // Image height in pixels
            'location' => [                  // Location data
                'latitude' => $latestLatitude,
                'longitude' => $latestLongitude
            ]
        ];
    }
}

// Return the list of images as JSON
echo json_encode($images);
?>