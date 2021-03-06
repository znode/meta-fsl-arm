# Copyright (C) 2012-2013 Freescale Semicondutors
# Released under the MIT license (see COPYING.MIT for the terms)
DESCRIPTION = "Freescale Multimedia parser libs"
LICENSE = "Proprietary"
SECTION = "multimedia"
LIC_FILES_CHKSUM = "file://EULA.txt;md5=ea4d5c069d7aef0838a110409ea78a01"

PR = "r1"

inherit fsl-eula-unpack autotools pkgconfig

SRC_URI[md5sum] = "9fd8105530e1668ae91bd53a5b0d9807"
SRC_URI[sha256sum] = "d3139e28e453d2af04439e607cd12ad17e117144049c9a8add05a5a142c654ae"
SRC_URI = "${FSL_MIRROR}/${PN}-${PV}.bin;fsl-eula=true"

do_install_append() {
	# FIXME: The only parser outside imx-mm/parse is the mp3 so move it
	mv ${D}${libdir}/*mp3* ${D}${libdir}/imx-mm/parser/
}

python populate_packages_prepend() {
    # FIXME: All binaries lack GNU_HASH in elf binary but as we don't have
    # the source we cannot fix it. Disable the insane check for now.
    # FIXME: gst-fsl-plugin looks for the .so files so we need to deploy those
    for p in d.getVar('PACKAGES', True).split():
        d.setVar("INSANE_SKIP_%s" % p, "ldflags dev-so")
}

# FIXME: gst-fsl-plugin looks for the .so files so we need to deploy those
FILES_${PN} += "${libdir}/imx-mm/*/*${SOLIBS} ${libdir}/imx-mm/*/*${SOLIBSDEV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "(mx6)"
