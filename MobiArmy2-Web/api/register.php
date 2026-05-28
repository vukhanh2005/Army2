<?php
declare(strict_types=1);

require_once __DIR__ . '/../includes/auth.php';
header('Content-Type: application/json; charset=utf-8');

if (($_POST['api_secret'] ?? '') !== API_SECRET) {
    http_response_code(403);
    echo json_encode(['success' => false, 'message' => 'Forbidden']);
    exit;
}

$result = register_account($_POST['username'] ?? '', $_POST['password'] ?? '');
echo json_encode($result, JSON_UNESCAPED_UNICODE);

