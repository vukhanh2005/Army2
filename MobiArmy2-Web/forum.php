<?php
require_once __DIR__ . '/includes/layout.php';
$user = current_user();
$message = null;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user = require_login();
    $title = trim($_POST['title'] ?? '');
    $body = trim($_POST['body'] ?? '');
    $category = $_POST['category'] === 'question' ? 'question' : 'discussion';
    if ($title === '' || $body === '') {
        $message = 'Bạn cần nhập tiêu đề và nội dung.';
    } else {
        $stmt = db()->prepare('INSERT INTO forum_threads (user_id, title, category, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())');
        $stmt->execute([$user['id'], $title, $category]);
        $threadId = (int) db()->lastInsertId();
        $stmt = db()->prepare('INSERT INTO forum_posts (thread_id, user_id, body, created_at) VALUES (?, ?, ?, NOW())');
        $stmt->execute([$threadId, $user['id'], $body]);
        header('Location: thread.php?id=' . $threadId);
        exit;
    }
}

$threads = db()->query('SELECT t.id, t.title, t.category, t.created_at, t.updated_at, u.username, COUNT(p.id) replies FROM forum_threads t JOIN user u ON u.id = t.user_id LEFT JOIN forum_posts p ON p.thread_id = t.id GROUP BY t.id ORDER BY t.updated_at DESC LIMIT 50')->fetchAll();
render_header('Hỏi đáp');
?>
<section class="panel">
    <h1>Hỏi đáp và diễn đàn</h1>
    <?php if ($message): ?><div class="alert error"><?= htmlspecialchars($message) ?></div><?php endif; ?>
    <?php if ($user): ?>
        <form method="post">
            <label>Loại bài
                <select name="category">
                    <option value="question">Hỏi đáp</option>
                    <option value="discussion">Thảo luận</option>
                </select>
            </label>
            <label>Tiêu đề <input name="title" maxlength="160" required></label>
            <label>Nội dung <textarea name="body" required></textarea></label>
            <button type="submit">Đăng bài</button>
        </form>
    <?php else: ?>
        <p><a href="login.php">Đăng nhập</a> để đăng bài hoặc trả lời.</p>
    <?php endif; ?>
</section>

<section class="panel">
    <h2>Bài viết mới</h2>
    <?php foreach ($threads as $thread): ?>
        <div class="thread">
            <a href="thread.php?id=<?= (int)$thread['id'] ?>"><strong><?= htmlspecialchars($thread['title']) ?></strong></a>
            <div class="muted"><?= htmlspecialchars($thread['category']) ?> bởi <?= htmlspecialchars($thread['username']) ?>, <?= (int)$thread['replies'] ?> phản hồi</div>
        </div>
    <?php endforeach; ?>
</section>
<?php render_footer(); ?>

