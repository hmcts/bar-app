output "idam_s2s_url" {
    value = "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.core-compute-${local.local_env}.internal"
}

output "payment_api_url" {
    value = "http://${var.payment_api_url_prefix}-${local.local_env}.service.core-compute-${local.local_env}.internal"
}