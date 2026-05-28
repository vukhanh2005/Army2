<?php
declare(strict_types=1);

require_once __DIR__ . '/db.php';

session_start();

function password_with_secret(string $password): string
{
    return PASSWORD_SECRET . ':' . $password;
}

function hash_password(string $password): string
{
    return password_hash(password_with_secret($password), PASSWORD_BCRYPT, ['cost' => 10]);
}

function verify_password(string $password, string $hash): bool
{
    if (password_verify(password_with_secret($password), $hash)) {
        return true;
    }

    return password_verify($password, $hash);
}

function current_user(): ?array
{
    if (empty($_SESSION['user_id'])) {
        return null;
    }

    $stmt = db()->prepare('SELECT id, username FROM user WHERE id = ?');
    $stmt->execute([$_SESSION['user_id']]);
    $user = $stmt->fetch();

    return $user ?: null;
}

function require_login(): array
{
    $user = current_user();
    if (!$user) {
        header('Location: login.php');
        exit;
    }

    return $user;
}

function is_valid_username(string $username): bool
{
    return preg_match('/^[a-zA-Z0-9_]{3,50}$/', $username) === 1;
}

function register_account(string $username, string $password): array
{
    $username = strtolower(trim($username));

    if (!is_valid_username($username)) {
        return ['success' => false, 'message' => 'Tên tài khoản chỉ gồm chữ, số, dấu _ và dài 3-50 ký tự.'];
    }

    if (strlen($password) < 3 || strlen($password) > 72) {
        return ['success' => false, 'message' => 'Mật khẩu phải dài từ 3 đến 72 ký tự.'];
    }

    $pdo = db();
    $stmt = $pdo->prepare('SELECT id FROM user WHERE LOWER(username) = LOWER(?)');
    $stmt->execute([$username]);
    if ($stmt->fetch()) {
        return ['success' => false, 'message' => 'Tên tài khoản đã tồn tại.'];
    }

    $pdo->beginTransaction();
    try {
        $stmt = $pdo->prepare('INSERT INTO user (username, password) VALUES (?, ?)');
        $stmt->execute([$username, hash_password($password)]);
        $userId = (int) $pdo->lastInsertId();

        $stmt = $pdo->prepare('INSERT INTO user_ (user_id, name, xu, luong, cup, glass) VALUES (?, ?, ?, ?, ?, ?)');
        $stmt->execute([$userId, $username, 1000, 1000, 0, 0]);

        $pdo->commit();
        return ['success' => true, 'message' => 'Đăng ký thành công.', 'user_id' => $userId];
    } catch (Throwable $ex) {
        $pdo->rollBack();
        return ['success' => false, 'message' => 'Không thể tạo tài khoản.'];
    }
}

