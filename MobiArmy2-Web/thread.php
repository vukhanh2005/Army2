<?php
require_once __DIR__ . '/includes/layout.php';
$id = (int)($_GET['id'] ?? 0);
$stmt = db()->prepare('SELECT t.*, u.username FROM forum_threads t JOIN user u ON u.id = t.user_id WHERE t.id = ?');
$stmt->execute([$id]);
$thread = $stmt->fetch();
if (!$thread) {
    http_response_code(404);
    exit('Không tìm thấy bài viết.');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user = require_login();
    $body = trim($_POST['body'] ?? '');
    if ($body !== '') {
        $stmt = db()->prepare('INSERT INTO forum_posts (thread_id, user_id, body, created_at) VALUES (?, ?, ?, NOW())');
        $stmt->execute([$id, $user['id'], $body]);
        db()->prepare('UPDATE forum_threads SET updated_at = NOW() WHERE id = ?')->execute([$id]);
        header('Location: thread.php?id=' . $id);
        exit;
    }
}

$stmt = db()->prepare('SELECT p.body, p.created_at, u.username FROM forum_posts p JOIN user u ON u.id = p.user_id WHERE p.thread_id = ? ORDER BY p.created_at');
$stmt->execute([$id]);
$posts = $stmt->fetchAll();
render_header($thread['title']);
?>
<section class="panel">
    <h1><?= htmlspecialchars($thread['title']) ?></h1>
    <p class="muted"><?= htmlspecialchars($thread['category']) ?> bởi <?= htmlspecialchars($thread['username']) ?></p>
    <?php foreach ($posts as $post): ?>
        <article class="thread">
            <div class="muted"><?= htmlspecialchars($post['username']) ?> - <?= htmlspecialchars($post['created_at']) ?></div>
            <p><?= nl2br(htmlspecialchars($post['body'])) ?></p>
        </article>
    <?php endforeach; ?>
</section>

<section class="panel">
    <h2>Trả lời</h2>
    <?php if (current_user()): ?>
        <form method="post">
            <textarea name="body" required></textarea>
            <button type="submit">Gửi trả lời</button>
        </form>
    <?php else: ?>
        <p><a href="login.php">Đăng nhập</a> để trả lời.</p>
    <?php endif; ?>
</section>
<?php render_footer(); ?>

