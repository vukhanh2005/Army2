<?php
require_once __DIR__ . '/includes/layout.php';
$user = require_login();

$stmt = db()->prepare('SELECT * FROM user_ WHERE user_id = ?');
$stmt->execute([$user['id']]);
$profile = $stmt->fetch() ?: [];

$stmt = db()->prepare('SELECT glassID, level, exp, point FROM user_glass WHERE user_id = ? ORDER BY glassID');
$stmt->execute([$user['id']]);
$glasses = $stmt->fetchAll();

$glassNames = ['Cannon', 'AK', 'Proton', 'Khỉ', 'Rocket', 'Granos', 'Gà', 'Tarzan', 'Apache', 'Laser'];
render_header('Tài khoản');
?>
<section class="panel">
    <h1><?= htmlspecialchars($profile['name'] ?? $user['username']) ?></h1>
    <div class="stats">
        <div class="stat">Xu<strong><?= number_format((int)($profile['xu'] ?? 0)) ?></strong></div>
        <div class="stat">Lượng<strong><?= number_format((int)($profile['luong'] ?? 0)) ?></strong></div>
        <div class="stat">Cúp<strong><?= number_format((int)($profile['cup'] ?? 0)) ?></strong></div>
        <div class="stat">Nhân vật đang chọn<strong><?= htmlspecialchars($glassNames[(int)($profile['glass'] ?? 0)] ?? 'Không rõ') ?></strong></div>
    </div>
</section>

<section class="panel">
    <h2>Nhân vật đã mở khóa</h2>
    <table>
        <tr><th>Nhân vật</th><th>Cấp</th><th>Kinh nghiệm</th><th>Điểm</th></tr>
        <?php foreach ($glasses as $glass): ?>
            <tr>
                <td><?= htmlspecialchars($glassNames[(int)$glass['glassID']] ?? ('Glass ' . $glass['glassID'])) ?></td>
                <td><?= (int)$glass['level'] ?></td>
                <td><?= number_format((int)$glass['exp']) ?></td>
                <td><?= (int)$glass['point'] ?></td>
            </tr>
        <?php endforeach; ?>
        <?php if (!$glasses): ?>
            <tr><td colspan="4">Tài khoản mới chỉ có nhân vật mặc định.</td></tr>
        <?php endif; ?>
    </table>
</section>
<?php render_footer(); ?>
