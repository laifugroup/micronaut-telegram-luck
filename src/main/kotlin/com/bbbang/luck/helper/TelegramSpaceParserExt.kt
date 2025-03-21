import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.core.TelegramSpaceParser
import java.util.*

// Delete:fun TelegramSpaceParser.parse(update: Update): Optional<Chat> {
// Delete:    ... existing code ...
// }

// Modify说明：将扩展函数改为在TelegramSpaceParserExt类中覆盖parse方法
class TelegramSpaceParserExt : TelegramSpaceParser() {
    override fun parse(update: Update): Optional<Chat> {
        val origin = if (update.editedMessage != null) {
            Optional.of(update.editedMessage!!.chat)
        } else if (update.editedChannelPost != null) {
            Optional.of(update.editedChannelPost!!.chat)
        } else if (update.channelPost != null) {
            Optional.of(update.channelPost!!.chat)
        } else if (update.callbackQuery?.message?.chat != null) {
            Optional.of(update.callbackQuery.message.chat)
        } else {
            if (update.message != null) Optional.of(update.message.chat) else Optional.empty()
        }
        return origin
    }
}