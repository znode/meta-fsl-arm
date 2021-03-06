# Copyright (C) 2012 Freescale Semicondutors
# Released under the MIT license (see COPYING.MIT for the terms)
DESCRIPTION = "Freescale Multimedia codec libs"
LICENSE = "Proprietary"
SECTION = "multimedia"
LIC_FILES_CHKSUM = "file://EULA.txt;md5=ea4d5c069d7aef0838a110409ea78a01"

PR = "r1"

inherit fsl-eula-unpack autotools pkgconfig

SRC_URI[md5sum] = "dc08b569175981fc0bd5f97986cfd543"
SRC_URI[sha256sum] = "2a4a7248246ff37f30486f34918151f09493ecfcaeca0cd8155d5b5c93245138"
SRC_URI = "${FSL_MIRROR}/${PN}-${PV}.bin;fsl-eula=true"

PACKAGES_DYNAMIC = "${PN}-*"

do_install_append() {
	# FIXME: This link points to nowhere
	rm ${D}${libdir}/imx-mm/audio-codec/lib_src_ppp_arm11_elinux.so

	# LTIB move the files around or gst-fsl-plugin won't find them
	for p in $(find ${D}${libdir}/imx-mm -mindepth 1 -maxdepth 1 -type d); do
		mv $p/* ${D}${libdir}
		rmdir $p
	done
	rmdir ${D}${libdir}/imx-mm

	# FIXME: Drop examples
	rm -r ${D}${datadir}/imx-mm
}

python populate_packages_prepend() {
    do_split_packages(d, d.getVar('libdir', True), '^lib_(.*)_elinux\.so\..*',
                      aux_files_pattern_verbatim='${libdir}/lib_%s_elinux.so.*',
                      output_pattern='libfslcodec-audio-%s',
                      description='Freescale IMX Codec (%s)',
                      extra_depends='', prepend=True)

    wrapdir = bb.data.expand('${libdir}/wrap', d)
    do_split_packages(d, wrapdir, '^lib_(.*)_elinux\.so\..*',
                      aux_files_pattern_verbatim='${libdir}/wrap/lib_%s_elinux.so.*',
                      output_pattern='libfslcodec-audio-wrap-%s',
                      description='Freescale IMX Codec Wrap (%s)',
                      extra_depends='', prepend=True)

    # FIXME: All binaries lack GNU_HASH in elf binary but as we don't have
    # the source we cannot fix it. Disable the insane check for now.
    for p in d.getVar('PACKAGES', True).split():
        d.setVar("INSANE_SKIP_%s" % p, "ldflags")
}

# Ensure we get warnings if we miss something
FILES_${PN} = ""

FILES_${PN}-dev += "${libdir}/*/*${SOLIBSDEV} \
                    ${libdir}/*/*/*${SOLIBSDEV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "(mx6)"
