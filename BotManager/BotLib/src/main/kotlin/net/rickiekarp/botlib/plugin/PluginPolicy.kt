package net.rickiekarp.botlib.plugin

import java.security.*

/**
 * Custom policy for the security-test.
 */
internal class PluginPolicy : Policy() {

    /**
     * Returns [AllPermission] for any code sources that do not end in
     * “/test.jar” and an empty set of permissions for code sources that do end
     * in “/test.jar”, denying access to all local resources to the rogue
     * plugin.
     *
     * @param codeSource The code source to get the permissiosn for
     * @return The permissions for the given code source
     */
    override fun getPermissions(codeSource: CodeSource): PermissionCollection {
        val p = Permissions()
        if (!codeSource.location.toString().endsWith("/test.jar")) {
            p.add(AllPermission())
        }
        return p
    }

    /**
     * Does nothing.
     */
    override fun refresh() {}

}
