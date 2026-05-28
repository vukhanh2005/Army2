<?php
require_once __DIR__ . '/includes/layout.php';
$message = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = strtolower(trim($_POST['username'] ?? ''));
    $password = $_POST['password'] ?? '';
    $stmt = db()->prepare('SELECT id, password FROM user WHERE LOWER(username) = LOWER(?)');
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    if (!$user || !verify_password($password, $user['password'])) {
        $message = 'Tài khoản hoặc mật khẩu không chính xác.';
    } else {
        if (!password_verify(password_with_secret($password), $user['password'])) {
            $stmt = db()->prepare('UPDATE user SET password = ? WHERE id = ?');
            $stmt->execute([hash_password($password), $user['id']]);
        }
        $_SESSION['user_id'] = (int) $user['id'];
        header('Location: account.php');
        exit;
    }
}

render_header('Đăng nhập');
?>
<section class="panel">
    <h1>Đăng nhập</h1>
    <?php if ($message): ?><div class="alert error"><?= htmlspecialchars($message) ?></div><?php endif; ?>
    <form method="post">
        <label>Tên tài khoản
            <input name="username" required>
        </label>
        <label>Mật khẩu
            <input name="password" type="password" required>
        </label>
        <button type="submit">Đăng nhập</button>
    </form>
</section>
<?php render_footer(); ?>

