[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$Description
)

$ErrorActionPreference = 'Stop'

# Define components and their configuration
$components = @(
    @{
        Name         = "android-app"
        Path         = "android-app"
        SpecsPath    = "android-app/specs"
        ExplicitKeys = @("android-app", "android app", "android")
        Keywords     = @("android", "jetpack compose", "compose", "screen", "ui", "viewmodel", "navigation", "android navigation", "retrofit", "hilt", "android storage", "mobile ui", "customer screen", "worker screen", "labour screen")
    },
    @{
        Name         = "api-gateway"
        Path         = "backend-microservices/api-gateway"
        SpecsPath    = "backend-microservices/api-gateway/specs"
        ExplicitKeys = @("api-gateway", "api gateway")
        Keywords     = @("api gateway", "gateway", "route", "routing", "gateway filter", "gateway configuration", "request forwarding")
    },
    @{
        Name         = "auth-service"
        Path         = "backend-microservices/auth-service"
        SpecsPath    = "backend-microservices/auth-service/specs"
        ExplicitKeys = @("auth-service", "auth service", "auth")
        Keywords     = @("login", "authentication", "authorization", "jwt", "oauth2", "credentials", "password", "otp", "token", "session", "access token", "refresh token", "authentication flow")
    },
    @{
        Name         = "user-service"
        Path         = "backend-microservices/user-service"
        SpecsPath    = "backend-microservices/user-service/specs"
        ExplicitKeys = @("user-service", "user service")
        Keywords     = @("user profile", "customer profile", "worker profile", "labour profile", "user status", "customer details", "worker details", "user role", "user management")
    },
    @{
        Name         = "booking-service"
        Path         = "backend-microservices/booking-service"
        SpecsPath    = "backend-microservices/booking-service/specs"
        ExplicitKeys = @("booking-service", "booking service")
        Keywords     = @("booking", "appointment", "scheduling", "rescheduling", "cancellation", "booking lifecycle", "labour assignment", "worker assignment", "dispatch")
    },
    @{
        Name         = "catalog-service"
        Path         = "backend-microservices/catalog-service"
        SpecsPath    = "backend-microservices/catalog-service/specs"
        ExplicitKeys = @("catalog-service", "catalog service")
        Keywords     = @("service catalog", "service category", "service listing", "labour offering", "service discovery", "service search", "category")
    },
    @{
        Name         = "payment-service"
        Path         = "backend-microservices/payment-service"
        SpecsPath    = "backend-microservices/payment-service/specs"
        ExplicitKeys = @("payment-service", "payment service")
        Keywords     = @("payment", "transaction", "billing", "invoice", "payment status", "payment processing", "pricing", "refund")
    },
    @{
        Name         = "review-service"
        Path         = "backend-microservices/review-service"
        SpecsPath    = "backend-microservices/review-service/specs"
        ExplicitKeys = @("review-service", "review service")
        Keywords     = @("review", "rating", "feedback", "customer review", "worker rating")
    },
    @{
        Name         = "tracking-service"
        Path         = "backend-microservices/tracking-service"
        SpecsPath    = "backend-microservices/tracking-service/specs"
        ExplicitKeys = @("tracking-service", "tracking service")
        Keywords     = @("tracking", "live location", "gps", "coordinates", "location tracking", "map", "worker location", "real-time location")
    },
    @{
        Name         = "notification-service"
        Path         = "backend-microservices/notification-service"
        SpecsPath    = "backend-microservices/notification-service/specs"
        ExplicitKeys = @("notification-service", "notification service")
        Keywords     = @("notification", "push notification", "sms", "alert", "in-app notification", "messaging notification", "kafka notification event")
    },
    @{
        Name         = "common-lib"
        Path         = "backend-microservices/common-lib"
        SpecsPath    = "backend-microservices/common-lib/specs"
        ExplicitKeys = @("common-lib", "common library")
        Keywords     = @("shared dto", "shared model", "common dto", "common exception", "shared exception", "kafka event contract", "event contract", "shared enum", "shared utility", "common library")
    }
)

$lowerDesc = $Description.ToLower()

# 1. Check explicit component mention (highest priority)
foreach ($comp in $components) {
    foreach ($exp in $comp.ExplicitKeys) {
        if ($lowerDesc -match "\b$exp\b") {
            $result = [PSCustomObject]@{
                status              = "RESOLVED"
                component           = $comp.Name
                component_path      = $comp.Path
                specs_path          = $comp.SpecsPath
                reason              = "Explicitly referenced component '$($comp.Name)' in description."
                affected_components = @($comp.Name)
            }
            $result | ConvertTo-Json -Compress
            exit 0
        }
    }
}

# 2. Score by keywords
$scores = @{}
foreach ($comp in $components) {
    $score = 0
    foreach ($kw in $comp.Keywords) {
        if ($lowerDesc -match [regex]::Escape($kw)) {
            $score += 3
        }
    }
    foreach ($kw in $comp.Keywords) {
        foreach ($word in ($kw -split '\s+')) {
            if ($word.Length -gt 3 -and $lowerDesc -match "\b$word\b") {
                $score += 1
            }
        }
    }
    if ($score -gt 0) {
        $scores[$comp.Name] = $score
    }
}

$matchedComps = $scores.GetEnumerator() | Sort-Object Value -Descending

if ($matchedComps.Count -eq 0) {
    $result = [PSCustomObject]@{
        status              = "AMBIGUOUS"
        component           = $null
        component_path      = $null
        specs_path          = $null
        reason              = "No component matching keywords found. Please specify the target component."
        affected_components = @()
    }
    $result | ConvertTo-Json -Compress
    exit 0
}

$topMatch = $matchedComps[0]
$topCompName = $topMatch.Key
$topScore = $topMatch.Value

# Check for cross-component or ambiguity (if multiple components have close high scores)
$significantMatches = @($matchedComps | Where-Object { $_.Value -ge ($topScore * 0.8) -and $_.Value -ge 2 })

if ($significantMatches.Count -gt 1) {
    $affected = @($significantMatches | ForEach-Object { $_.Key })
    $result = [PSCustomObject]@{
        status              = "CROSS_COMPONENT"
        component           = $null
        component_path      = $null
        specs_path          = $null
        reason              = "Requirement affects multiple components: $($affected -join ', '). Please select the primary owning component."
        affected_components = $affected
    }
    $result | ConvertTo-Json -Compress
    exit 0
}

# Single clear match
$resolvedComp = $components | Where-Object { $_.Name -eq $topCompName } | Select-Object -First 1
$result = [PSCustomObject]@{
    status              = "RESOLVED"
    component           = $resolvedComp.Name
    component_path      = $resolvedComp.Path
    specs_path          = $resolvedComp.SpecsPath
    reason              = "Matched component '$($resolvedComp.Name)' based on keywords."
    affected_components = @($resolvedComp.Name)
}
$result | ConvertTo-Json -Compress
exit 0
