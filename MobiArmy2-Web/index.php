<?php
require_once __DIR__ . '/includes/layout.php';
render_header('Trang chủ');
$top = db()->query('SELECT u.username, p.name, p.cup FROM user u JOIN user_ p ON p.user_id = u.id ORDER BY p.cup DESC, p.xu DESC LIMIT 5')->fetchAll();
$threads = db()->query('SELECT t.id, t.title, t.category, t.created_at, u.username FROM forum_threads t JOIN user u ON u.id = t.user_id ORDER BY t.updated_at DESC LIMIT 5')->fetchAll();
?>
<section class="panel">
    <h1>Diễn đàn MobiArmy</h1>
    <p>Nơi đăng ký tài khoản, xem hồ sơ nhân vật, hỏi đáp, theo dõi bảng xếp hạng và tin cộng đồng.</p>
</section>

<section class="grid">
    <div class="card">
        <h2>Tài khoản</h2>
        <p>Đăng nhập để xem xu, lượng, cúp, cấp và nhân vật đã mở khóa.</p>
        <a class="button" href="account.php">Xem tài khoản</a>
    </div>
    <div class="card">
        <h2>Hỏi đáp</h2>
        <p>Đặt câu hỏi, chia sẻ kinh nghiệm căn góc, item và chiến thuật.</p>
        <a class="button" href="forum.php">Vào diễn đàn</a>
    </div>
    <div class="card">
        <h2>Bảng xếp hạng</h2>
        <p>Theo dõi người chơi mạnh nhất theo cúp, xu và lượng.</p>
        <a class="button" href="ranking.php">Xem xếp hạng</a>
    </div>
</section>

<section class="grid">
    <div class="card">
        <h2>Top cúp</h2>
        <?php foreach ($top as $row): ?>
            <p><strong><?= htmlspecialchars($row['name'] ?: $row['username']) ?></strong> <span class="muted"><?= number_format((int)$row['cup']) ?> cúp</span></p>
        <?php endforeach; ?>
    </div>
    <div class="card">
        <h2>Bài mới</h2>
        <?php foreach ($threads as $thread): ?>
            <p><a href="thread.php?id=<?= (int)$thread['id'] ?>"><?= htmlspecialchars($thread['title']) ?></a><br><span class="muted"><?= htmlspecialchars($thread['category']) ?> bởi <?= htmlspecialchars($thread['username']) ?></span></p>
        <?php endforeach; ?>
    </div>
</section>
<?php render_footer(); ?>

