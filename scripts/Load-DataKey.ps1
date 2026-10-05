param([switch]$Create, [string]$KeyDirectory = (Join-Path $env:LOCALAPPDATA 'MindCare/secrets'))
$ErrorActionPreference = 'Stop'
if (-not $IsWindows -and $PSVersionTable.PSEdition -eq 'Core') { throw 'Este auxiliar usa DPAPI do Windows.' }
$keyFile = Join-Path $keyDirectory 'record-key.dpapi'
if ($Create) {
    if (Test-Path -LiteralPath $keyFile) { throw 'Uma chave já existe. Ela não será substituída.' }
    New-Item -ItemType Directory -Path $keyDirectory -Force | Out-Null
    $bytes = New-Object byte[] 32
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
    $plainKey = [Convert]::ToBase64String($bytes)
    $secureKey = ConvertTo-SecureString $plainKey -AsPlainText -Force
    $protectedKey = ConvertFrom-SecureString $secureKey
    # CreateNew prevents accidental replacement of an existing key, including concurrent execution.
    $stream = [IO.File]::Open($keyFile, [IO.FileMode]::CreateNew, [IO.FileAccess]::Write)
    try {
        $writer = [IO.StreamWriter]::new($stream)
        try { $writer.Write($protectedKey) } finally { $writer.Dispose() }
    } finally { $stream.Dispose() }
    [Array]::Clear($bytes, 0, $bytes.Length)
    $plainKey = $null
}
if (-not (Test-Path -LiteralPath $keyFile)) { throw 'Chave ausente. Use -Create apenas para um banco que ainda não foi criptografado.' }
$secureKey = ConvertTo-SecureString (Get-Content -LiteralPath $keyFile -Raw)
$credential = [PSCredential]::new('mindcare', $secureKey)
$env:MINDCARE_DATA_KEY = $credential.GetNetworkCredential().Password
Write-Host 'Chave carregada neste terminal. Não compartilhe seu valor nem a grave no repositório.'
Write-Host 'Antes de migrar dados, guarde uma cópia recuperável da chave em um gerenciador de segredos.'
