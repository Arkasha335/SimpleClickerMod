// ... (весь остальной код ClientEventHandler остается без изменений) ...

@SubscribeEvent
public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
    if (!ModConfig.hudEnabled || mc.gameSettings.showDebugInfo) return;
    
    int yOffset = 5;

    mc.fontRendererObj.drawStringWithShadow("Clicker: " + (ModConfig.modEnabled ? "§aON" : "§cOFF"), 5, yOffset, 0xFFFFFF);
    yOffset += 10;
    
    if (ModConfig.bridgerEnabled && ModConfig.currentBridgeMode != BridgeMode.DISABLED) {
        String bridgerStatusText;
        switch(bridgeController.getCurrentState()) {
            case ARMED: bridgerStatusText = "§eARMED"; break;
            case BRIDGING:
            case STABILIZING:
                bridgerStatusText = "§cBRIDGING"; break;
            default:
                // --- НОВОЕ: Визуальный фидбек о готовности ---
                // Показываем, готов ли игрок, даже в состоянии IDLE
                bridgerStatusText = bridgeController.isReady() ? "§aREADY" : "§7IDLE";
                break;
        }
        mc.fontRendererObj.drawStringWithShadow("Bridger: " + bridgerStatusText, 5, yOffset, 0xFFFFFF);
        
        if(bridgeController.getCurrentState() != BridgeController.State.IDLE) {
             mc.fontRendererObj.drawStringWithShadow("§7Mode: " + ModConfig.currentBridgeMode.getDisplayName(), 5, yOffset + 10, 0xFFFFFF);
        }
    }
}