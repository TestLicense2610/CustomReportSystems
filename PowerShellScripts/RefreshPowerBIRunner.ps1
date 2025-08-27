param(
    [string]$user,
    [string]$password,
    [string]$workspaceName,
    [string]$datasetName
)
# Chắc chắn rằng các module đã được import
Import-Module MicrosoftPowerBIMgmt.Data -Force
Import-Module MicrosoftPowerBIMgmt.Profile -Force
try {
    $credential = New-Object System.Management.Automation.PSCredential($user, ($password | ConvertTo-SecureString -AsPlainText -Force))
    Connect-PowerBIServiceAccount -Credential $credential
    $workspace = Get-PowerBIWorkspace -Name $workspaceName -ErrorAction Stop
    $dataset = Get-PowerBIDataset -Name $datasetName -ErrorAction Stop
    Start-PowerBIDatasetRefresh -Id $dataset.Id -WorkspaceId $workspace.Id
    Write-Host "Success: Dataset refresh command sent successfully."
    exit 0
} catch {
    Write-Error $_.Exception.Message
    exit 1
}