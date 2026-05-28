<?php
require_once __DIR__ . '/includes/layout.php';
$message = null;
$error = false;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $result = register_account($_POST['username'] ?? '', $_POST['password'] ?? '');
    $message = $result['message'];
    $error = !$result['success'];
}

render_header('Đăng ký');
?>
<section class="panel">
    <h1>Đăng ký tài khoản</h1>
    <?php if ($message): ?><div class="alert <?= $error ? 'error' : '' ?>"><?= htmlspecialchars($message) ?></div><?php endif; ?>
    <form method="post">
        <label>Tên tài khoản
            <input name="username" maxlength="50" required>
        </label>
        <label>Mật khẩu
            <input name="password" type="password" maxlength="72" required>
        </label>
        <button type="submit">Tạo tài khoản</button>
    </form>
</section>
<?php render_footer(); ?>

