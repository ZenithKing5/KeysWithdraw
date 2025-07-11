# KeysWithdraw

 KeysWithdraw is a lightweight, feature rich and highly customizable keyswithdraw plugin made for ExcellentCrates.
 
 Commands:
  withdrawkeys:
    description: Withdraw keys as vouchers
    aliases: [wk]
    usage: /withdrawkeys <keyname> <amount>
  adminwithdrawkeys:
    description: Admin commands for key withdraw
    aliases: [awk]
    usage: /adminwithdrawkeys <create|delete|reload> [keyname]

Permissions:
  keywithdraw.use:
    description: Allows players to use the /withdrawkeys command
  keywithdraw.admin 
  description: Allows access to all admin commands

Note:

This plugin requires PlaceholderAPI and ExcellentCrates to be installed on the server alongside with this plugin.

The crate key that you are adding to this plugin must has the same name as the crate it is connected to otherwise it will not work as expected.
