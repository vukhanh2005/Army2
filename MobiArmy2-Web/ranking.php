<?php
require_once __DIR__ . '/includes/layout.php';
$type = $_GET['type'] ?? 'cup';
$allowed = ['cup' => 'cup', 'xu' => 'xu', 'luong' => 'luong'];
$column = $allowed[$type] ?? 'cup';
$stmt = db()->query("SELECT u.username, p.name, p.cup, p.xu, p.luong FROM user u JOIN user_ p ON p.user_id = u.id ORDER BY p.{$column} DESC LIMIT 100");
$rows = $stmt->fetchAll();
render_header('Bảng xếp hạng');
?>
<section class="panel">
    <h1>Bảng xếp hạng</h1>
    <p><a href="?type=cup">Cúp</a> | <a href="?type=xu">Xu</a> | <a href="?type=luong">Lượng</a></p>
    <table>
        <tr><th>#</th><th>Nhân vật</th><th>Cúp</th><th>Xu</th><th>Lượng</th></tr>
        <?php foreach ($rows as $i => $row): ?>
            <tr>
                <td><?= $i + 1 ?></td>
                <td><?= htmlspecialchars($row['name'] ?: $row['username']) ?></td>
                <td><?= number_format((int)$row['cup']) ?></td>
                <td><?= number_format((int)$row['xu']) ?></td>
                <td><?= number_format((int)$row['luong']) ?></td>
            </tr>
        <?php endforeach; ?>
    </table>
</section>
<?php render_footer(); ?>

