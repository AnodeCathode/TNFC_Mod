/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [23/06/2016, 17:43:40 (GMT)]
 */
package vazkii.quark.experimental.features;

import org.apache.commons.io.output.NullOutputStream;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

import java.io.PrintStream;
import java.util.function.Consumer;

public class PrintSuppressor extends Feature {

	public static boolean suppressOut, suppressErr;

	@Override
	public void setupConfig() {
		suppressOut = loadPropBool("Suppress STDOUT", "", true);
		suppressErr = loadPropBool("Suppress STDERR", "", false);
	}

	@Override
	public void postInit() {
		Quark.LOG.warn("Quark print suppression is enabled. Important info might be missing. Suppressing STDOUT=%b, STDERR=%b", suppressOut, suppressErr);

		if(suppressOut)
			oppressFreedomOfSpeech(System::setOut);
		if(suppressErr)
			oppressFreedomOfSpeech(System::setErr);
	}

	private void oppressFreedomOfSpeech(Consumer<PrintStream> consumer) {
		PrintStream oppressedStream = new PrintStream(new NullOutputStream());

		// Accept your oppression, consumer
		consumer.accept(oppressedStream);
	}

	@Override
	public String getFeatureDescription() {
		return "Suppresses all STDOUT (and STDERR, if enabled) messages so they don't show up in the console."
				+ "\nUse this if some mod left behind debug messages and you don't want to see them."
				+ "\nIf important messages end up being disabled by this, tell the modders in case to switch to a proper logger.";
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
