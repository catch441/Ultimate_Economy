package org.ue.common.utils;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SaveFileUtilsTest {

	private class AbstractUtils extends SaveFileUtils {
	}

	@Test
	public void saveErrorTest() throws IOException {
		SaveFileUtils utils = new AbstractUtils();
		utils.config = mock(YamlConfiguration.class);
		utils.file = mock(File.class);

		IOException e = mock(IOException.class);
		when(e.getMessage()).thenReturn("my error");
		doThrow(e).when(utils.config).save(utils.file);
		utils.save();
		verify(e).getMessage();
	}

	@Test
	public void createFileErrorTest() throws IOException {
		SaveFileUtils utils = new AbstractUtils();
		File savefile = mock(File.class);
		IOException e = mock(IOException.class);
		when(e.getMessage()).thenReturn("my error");
		doThrow(e).when(savefile).createNewFile();
		utils.createFile(savefile);
		verify(e).getMessage();
	}
}
