<?php
declare(strict_types=1);

const APP_NAME = 'MobiArmy Forum';
const PASSWORD_SECRET = 'NguyenVuKhanhEni';
const API_SECRET = 'NguyenVuKhanhEni';

$DB_HOST = getenv('MOBIARMY_DB_HOST') ?: '127.0.0.1';
$DB_NAME = getenv('MOBIARMY_DB_NAME') ?: 'army';
$DB_USER = getenv('MOBIARMY_DB_USER') ?: 'root';
$DB_PASS = getenv('MOBIARMY_DB_PASSWORD') ?: '';

