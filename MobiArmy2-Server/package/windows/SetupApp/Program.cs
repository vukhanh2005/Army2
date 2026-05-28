using System.Diagnostics;
using System.IO.Compression;
using System.Reflection;

const string appName = "MobiArmy2 Server";
string installDir = args.Length > 0 && !string.IsNullOrWhiteSpace(args[0])
    ? Path.GetFullPath(args[0])
    : Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles), "MobiArmy2Server");

Console.WriteLine($"{appName} setup");
Console.WriteLine($"Install directory: {installDir}");

Directory.CreateDirectory(installDir);

string payloadPath = Path.Combine(Path.GetTempPath(), $"mobiarmy2-server-{Guid.NewGuid():N}.zip");
try
{
    await using Stream? payload = Assembly.GetExecutingAssembly().GetManifestResourceStream("payload.zip");
    if (payload == null)
    {
        throw new InvalidOperationException("payload.zip was not embedded into setup.exe.");
    }

    await using (FileStream output = File.Create(payloadPath))
    {
        await payload.CopyToAsync(output);
    }

    ZipFile.ExtractToDirectory(payloadPath, installDir, overwriteFiles: true);
    Console.WriteLine("Server files extracted.");
}
finally
{
    TryDelete(payloadPath);
}

if (Run("netsh", "advfirewall firewall add rule name=\"MobiArmy2 Server 8122\" dir=in action=allow protocol=TCP localport=8122"))
{
    Console.WriteLine("Firewall rule for TCP 8122 added.");
}
else
{
    Console.WriteLine("WARNING: Could not add firewall rule. Run setup.exe as Administrator or open TCP 8122 manually.");
}

Console.WriteLine();
Console.WriteLine("Install complete.");
Console.WriteLine($"Edit DB config: {Path.Combine(installDir, "server.properties")}");
Console.WriteLine($"Start server:   {Path.Combine(installDir, "run-server.bat")}");

static bool Run(string fileName, string arguments, bool quiet = false)
{
    try
    {
        using Process process = Process.Start(new ProcessStartInfo
        {
            FileName = fileName,
            Arguments = arguments,
            UseShellExecute = false,
            CreateNoWindow = quiet,
            RedirectStandardOutput = quiet,
            RedirectStandardError = quiet
        })!;
        process.WaitForExit();
        return process.ExitCode == 0;
    }
    catch
    {
        return false;
    }
}

static void TryDelete(string path)
{
    try
    {
        if (File.Exists(path))
        {
            File.Delete(path);
        }
    }
    catch
    {
        // Temporary file cleanup failure should not break installation.
    }
}
