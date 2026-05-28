<?php
declare(strict_types=1);

require_once __DIR__ . '/auth.php';

function render_header(string $title): void
{
    $user = current_user();
    ?>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><?= htmlspecialchars($title) ?> - <?= APP_NAME ?></title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="index.php">MobiArmy Forum</a>
    <nav>
        <a href="ranking.php">Bảng xếp hạng</a>
        <a href="forum.php">Hỏi đáp</a>
        <?php if ($user): ?>
            <a href="account.php">Tài khoản</a>
            <a href="logout.php">Thoát</a>
        <?php else: ?>
            <a href="login.php">Đăng nhập</a>
            <a class="button" href="register.php">Đăng ký</a>
        <?php endif; ?>
    </nav>
</header>
<main class="page">
<?php
}

function render_footer(): void
{
    ?>
</main>
<footer class="footer">MobiArmy community hub</footer>
</body>
</html>
<?php
}

